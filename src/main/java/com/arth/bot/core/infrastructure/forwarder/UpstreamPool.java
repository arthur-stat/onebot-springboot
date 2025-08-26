package com.arth.bot.core.infrastructure.forwarder;

import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.arth.bot.core.common.util.ForwardMatcherRegistry;
import com.arth.bot.core.config.ForwarderConfig;
import com.arth.bot.core.infrastructure.forwarder.matcher.ForwardMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpstreamPool {

    private final ForwardMatcherRegistry forwardMatcherRegistry;
    private final Sender sender;

    /* ========= 一个 URL 对应唯一一个 WebSocket 客户端 ========= */
    private final ConcurrentMap<String, UpstreamClient> clients = new ConcurrentHashMap<>();

    public void forward(ForwarderConfig.ForwarderProperties.Target target, ParsedPayloadDTO payload) {
        ForwardMatcher matcher = forwardMatcherRegistry.resolve(target.getMatcher());
        if (matcher == null || payload == null) return;
        if (!matcher.matches(payload)) return;

        String originalJsonString = payload.getOriginalJsonString();
        if (originalJsonString == null || originalJsonString.isEmpty()) return;

        String url = target.getUrl();
        long selfId = payload.getSelfId();
        UpstreamClient client = clients.computeIfAbsent(url, key -> createAndConnect(target, selfId));

        client.enqueue(originalJsonString);
    }

    private UpstreamClient createAndConnect(ForwarderConfig.ForwarderProperties.Target target, long selfId) {
        UpstreamClient upstreamClient = new UpstreamClient(target, selfId);
        upstreamClient.connectAsync();  // asyn handshake
        return upstreamClient;
    }

    private String buildOrigin(ForwarderConfig.ForwarderProperties.Target target) {
        String targetOrigin = target.getOrigin();
        if (targetOrigin != null && !(targetOrigin = targetOrigin.trim()).isEmpty()) {
            if ("none".equalsIgnoreCase(targetOrigin)) return null;
            if (!"auto".equalsIgnoreCase(targetOrigin))
                return targetOrigin.startsWith("http") ? targetOrigin : "http://" + targetOrigin;
        }
        try {
            var u = java.net.URI.create(target.getUrl());
            String scheme = "wss".equalsIgnoreCase(u.getScheme()) ? "https" : "http";
            String host = (u.getHost() != null) ? u.getHost() : u.getAuthority();
            if (host == null || host.isBlank()) return null;
            if (host.indexOf(':') >= 0 && !(host.startsWith("[") && host.endsWith("]"))) host = "[" + host + "]";
            int p = u.getPort();
            boolean def = p == -1 || ("http".equals(scheme) && p == 80) || ("https".equals(scheme) && p == 443);
            return def ? scheme + "://" + host : scheme + "://" + host + ":" + p;
        } catch (Exception e) {
            return null;
        }
    }

    final class UpstreamClient implements WebSocketHandler {

        private final String url;
        private final long selfId;
        private final ForwarderConfig.ForwarderProperties.Target target;
        private final AtomicReference<WebSocketSession> ref = new AtomicReference<>();
        private final BlockingQueue<String> outQ = new LinkedBlockingQueue<>(1000);
        private final Thread writer = new Thread(this::writeLoop, "up-writer-" + System.identityHashCode(this));

        UpstreamClient(ForwarderConfig.ForwarderProperties.Target target, long selfId) {
            this.target = target;
            url = target.getUrl();
            Long targetSelfId = target.getSelfId();
            if (targetSelfId != null) {
                this.selfId = targetSelfId;
            } else {
                this.selfId = selfId;
            }

            writer.setDaemon(true);
            writer.start();
            log.info("[forwarder] websocket upstream client created, target url: {}", url);
        }

        void connectAsync() {
            var client = new StandardWebSocketClient();
            var headers = new WebSocketHttpHeaders();

            headers.setSecWebSocketProtocol(target.getSecWebSocketProtocol());
            headers.setOrigin(buildOrigin(target));
            headers.add("X-Self-ID", String.valueOf(selfId));
            headers.add("X-Client-Role", target.getRole());
            String token = target.getToken();
            if (token != null && !token.isBlank()) {
                headers.add("Authorization", "Bearer " + token);
            }

            client.execute(this, headers, URI.create(url))
                    .thenAccept(s -> log.info("[forwarder] connected: {}", url))
                    .exceptionally(e -> {
                        Throwable t = (e instanceof java.util.concurrent.CompletionException
                                || e instanceof java.util.concurrent.ExecutionException) && e.getCause() != null
                                ? e.getCause() : e;
                        Throwable root = NestedExceptionUtils.getMostSpecificCause(t);

                        log.warn("[forwarder] connect failed url={} : {}", url, root.toString());

//                        if (log.isDebugEnabled()) {
//                            log.debug("[forwarder] connect failed stack url={}", url, e);
//                        }
                        return null;
                    });
        }

        void enqueue(String json) {
            if (!outQ.offer(json)) {
                log.warn("[forwarder] drop (queue full) url={}, len={}", url, json.length());
            }
        }

        private void writeLoop() {
            while (true) {
                try {
                    String json = outQ.take();  // 阻塞等待
                    WebSocketSession s = ref.get();
                    if (s == null || !s.isOpen()) {
                        // 未连接
                        continue;
                    }
                    synchronized (s) {
                        s.sendMessage(new TextMessage(json));
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (IOException io) {
                    log.warn("[forwarder] send error {}: {}", url, io.toString());
                }
            }
        }

        /* ====== 回程转发 ====== */
        @Override
        public void handleMessage(WebSocketSession upstream, WebSocketMessage<?> msg) {
            String payload = String.valueOf(msg.getPayload());
            sender.sendRawJSON(selfId, payload);
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            ref.set(session);
            session.getAttributes().put("onebot_id", 0L);
            log.info("[forwarder] websocket connection established, session id: {}, target url: {}", session.getId(), url);
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) {
            Throwable root = NestedExceptionUtils.getMostSpecificCause(exception);
            log.warn("[forwarder] transport error url={} : {}", url, root.toString());
            if (log.isDebugEnabled()) {
                log.debug("[forwarder] transport stack url={}", url, exception);
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
            ref.compareAndSet(session, null);
            log.info("[forwarder] closed {}: {}", url, closeStatus);
        }

        @Override
        public boolean supportsPartialMessages() {
            return false;
        }
    }
}
