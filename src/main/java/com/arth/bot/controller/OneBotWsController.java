package com.arth.bot.controller;

import com.arth.bot.common.dto.ParsedPayloadDTO;
import com.arth.bot.infrastructure.forwarder.ForwardMessageQueue;
import com.arth.bot.application.routing.ParseAndRouteService;
import com.arth.bot.infrastructure.websocket.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ExecutorService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OneBotWsController extends TextWebSocketHandler {

    private final ParseAndRouteService parseAndRouteService;
    private final SessionRegistry sessionRegistry;
    private final ForwardMessageQueue forwardMessageQueue;
    private final ExecutorService executorService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("websocket connection established, session ID: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String raw = message.getPayload();
            ParsedPayloadDTO dto = parseAndRouteService.parseRawToDTO(raw);

            if (session.getAttributes().get("self_id") == null) {
                long selfId = dto.getSelfId();
                sessionRegistry.put(selfId, session);
                session.getAttributes().put("self_id", selfId);
            }

            forwardMessageQueue.offer(dto);

            /* 多线程异步解析命令 */
            executorService.execute(() -> {
                try {
                    var outs = parseAndRouteService.initialRoute(dto);
                    if (outs != null && !outs.isEmpty() && session.isOpen()) {
                        synchronized (session) {
                            for (String json : outs) {
                                session.sendMessage(new TextMessage(json));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("async handle error", e);
                }
            });

        } catch (Exception e) {
            log.error("WebSocket pipeline error", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("transport error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.remove((Long) session.getAttributes().get("self_id"));
        log.info("websocket connection closed, session ID: {}", session.getId());
    }
}