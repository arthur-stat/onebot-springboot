package com.arth.bot.controller;

import com.arth.bot.dto.ParsedPayloadDTO;
import com.arth.bot.service.ParseAndRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Slf4j
@Controller
public class OneBotWsController extends TextWebSocketHandler {

    private final ParseAndRouteService parseAndRouteService;

    public OneBotWsController(ParseAndRouteService parseAndRouteService) {
        this.parseAndRouteService = parseAndRouteService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("websocket connection established, session ID: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String rawJson = message.getPayload();
        String sessionId = session.getId();
        log.debug("received: {}", rawJson);

        ParsedPayloadDTO dto = parseAndRouteService.parsedRawToDTO(rawJson);
        List<String> actionJSONs = parseAndRouteService.routingAndReturn(dto, sessionId);

        if (actionJSONs != null && !actionJSONs.isEmpty()) {
            synchronized (session) {
                for (String actionJSON : actionJSONs) {
                    session.sendMessage(new TextMessage(actionJSON));
                    log.debug("sent: {}", actionJSON);
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error", exception);
    }
}