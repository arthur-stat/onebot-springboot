package com.arth.bot.controller;

import com.arth.bot.common.exception.BusinessException;
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

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OneBotWsController extends TextWebSocketHandler {

    private final ParseAndRouteService parseAndRouteService;
    private final SessionRegistry sessionRegistry;
    private final ForwardMessageQueue forwardMessageQueue;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("websocket connection established, session ID: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {



        try {
            String rawJson = message.getPayload();
            log.debug("received: {}", rawJson);

            ParsedPayloadDTO dto = parseAndRouteService.parsedRawToDTO(rawJson);
            /* 仅在接收到首个消息时，将 self_id 绑定至相应的 session，以便其他模块例如业务异常处理器通过 ws session 控制 bot 发送消息 */
            if (session.getAttributes().get("self_id") == null) {
                long selfId = dto.getSelfId();
                sessionRegistry.put(selfId, session);
                session.getAttributes().put("self_id", selfId);
            }

            /* 转发器的消息队列 */
            forwardMessageQueue.offer(dto);

            List<String> actionJSONs = parseAndRouteService.parsedAndRouting(dto);
            if (actionJSONs != null && !actionJSONs.isEmpty()) {
                synchronized (session) {
                    for (String actionJSON : actionJSONs) {
                        session.sendMessage(new TextMessage(actionJSON));
                        log.debug("sent: {}", actionJSON);
                    }
                }
            }
        } catch (BusinessException be) {
            log.warn("business error: {}", be.getMessage());
        } catch (Exception e) {
            log.error("WS pipeline error", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionRegistry.remove((Long) session.getAttributes().get("self_id"));
        log.info("websocket connection closed, session ID: {}", session.getId());
    }
}