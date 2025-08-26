package com.arth.bot.adapter.sender.impl;

import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.adapter.sender.action.ActionBuilder;
import com.arth.bot.adapter.session.SessionRegistry;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class OneBotSender implements Sender {

    private final SessionRegistry sessions;
    private final ActionBuilder actionBuilder;

    @Override
    public void send(ParsedPayloadDTO payload, Object o) {

    }

    @Override
    public void sendText(ParsedPayloadDTO payload, Object text) {
        WebSocketSession session = sessions.get(payload.getSelfId());
        if (session == null || !session.isOpen() || text == null) return;

        synchronized (session) {
            try {
                if (text instanceof Iterable<?> it) {
                    for (Object o : it) sendTextOnce(session, payload, o);
                } else {
                    sendTextOnce(session, payload, text);
                }
            } catch (Exception e) {
                log.warn("[adapter] send text failed", e);
            }
        }
    }

    @Override
    public void sendRawJSON(long selfId, String json) {
        WebSocketSession session = sessions.get(selfId);
        if (session == null || !session.isOpen()) {
            log.warn("[adapter] raw json send: session missing/closed, selfId={}", selfId);
            return;
        }
        synchronized (session) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.warn("[adapter] raw json send failed, selfId={}", selfId, e);
            }
        }
    }

    private void sendTextOnce(WebSocketSession session, ParsedPayloadDTO payload, Object o) throws Exception {
        if (o == null) return;
        String json;

        if (o instanceof String s) {
            json = payload.getGroupId() != null
                    ? actionBuilder.buildGroupSendTextAction(payload.getGroupId(), s)
                    : actionBuilder.buildPrivateSendTextAction(payload.getUserId(), s);
        } else {
            String text = String.valueOf(o);
            json = payload.getGroupId() != null
                    ? actionBuilder.buildGroupSendTextAction(payload.getGroupId(), text)
                    : actionBuilder.buildPrivateSendTextAction(payload.getUserId(), text);
        }

        log.debug("[adapter] sending message: {}", json);
        session.sendMessage(new TextMessage(json));
    }
}