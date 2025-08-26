package com.arth.bot.adapter.controller;

import com.arth.bot.adapter.parser.PayloadParser;
import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.adapter.session.SessionRegistry;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.arth.bot.core.infrastructure.forwarder.ForwardMessageQueue;
import com.arth.bot.core.routing.CommandInvoker;
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

    private final SessionRegistry sessionRegistry;
    private final ForwardMessageQueue forwardMessageQueue;
    private final ExecutorService executorService;
    private final CommandInvoker commandInvoker;
    private final Sender sender;
    private final PayloadParser payloadParser;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("[adapter] ws connected: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String rowPayload = message.getPayload();
            ParsedPayloadDTO dto = payloadParser.parseRawToDTO(rowPayload);
            if (session.getAttributes().get("self_id") == null) {
                long selfId = dto.getSelfId();
                sessionRegistry.put(selfId, session);
                session.getAttributes().put("self_id", selfId);
            }

            log.debug("[adapter] received message: {}]", rowPayload);

            /* 转发器 */
            forwardMessageQueue.offer(dto);

            /* 多线程异步解析命令 */
            executorService.execute(() -> {
                try {
                    Object res = commandInvoker.parseAndInvoke(dto);
                    sender.sendText(dto, res);
                } catch (Exception e) {
                    log.error("[adapter] async handle error", e);
                }
            });

        } catch (Exception e) {
            log.error("[adapter] ws pipeline error", e);
        }
    }

//    private void writeResultToSession(WebSocketSession session, Object res) {
//        if (res == null || !session.isOpen()) return;
//        synchronized (session) {
//            try {
//                if (res instanceof Iterable<?> it) {
//                    for (Object o : it) {
//                        if (o == null) continue;
//                        session.sendMessage(new TextMessage(String.valueOf(o)));
//                    }
//                } else {
//                    session.sendMessage(new TextMessage(String.valueOf(res)));
//                }
//            } catch (Exception e) {
//                log.warn("[adapter] send failed", e);
//            }
//        }
//    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("[adapter] transport error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object sid = session.getAttributes().get("self_id");
        if (sid instanceof Long selfId) sessionRegistry.remove(selfId);
        log.info("[adapter] ws closed: {}", session.getId());
    }
}
