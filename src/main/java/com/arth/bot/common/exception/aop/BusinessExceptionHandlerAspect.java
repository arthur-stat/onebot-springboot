package com.arth.bot.common.exception.aop;

import com.arth.bot.common.exception.*;
import com.arth.bot.common.dto.ParsedPayloadDTO;
import com.arth.bot.common.util.ActionBuildUtil;
import com.arth.bot.infrastructure.websocket.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * BusinessException 的异常拦截器，包含可能的由 bot 向用户主动通知可读错误信息的逻辑
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BusinessExceptionHandlerAspect {

    private final SessionRegistry sessionRegistry;
    private final ActionBuildUtil actionBuildUtil;

    /**
     * 切点范围为以参数名 payload 作为首个参数的 service 下的所有类的全部方法
     * @param joinPoint
     * @param payload
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.arth.bot.service..*.*(..)) && args(payload, ..)")
    public Object around(ProceedingJoinPoint joinPoint, ParsedPayloadDTO payload) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (PermissionDeniedException e) {
            log.debug("permission denied: {}", e.getMessage());
            sendBusinessExceptionBack(e, payload);
            return null;
        } catch (InvalidCommandArgsException e) {
            log.debug("invalid command args: {}", e.getMessage());
            sendBusinessExceptionBack(e, payload);
            return null;
        } catch (CommandNotFoundException e) {
            log.debug("command not found: {}", e.getMessage());
            sendBusinessExceptionBack(e, payload);
            return null;
        } catch (BusinessException e) {
            log.warn("business error: {}", e.getMessage(), e);
            return null;
        }
    }

    private void sendBusinessExceptionBack(BusinessException e, ParsedPayloadDTO payload) {
        System.out.println(222);
        String description = e.getDescription();
        if (description == null || description.isEmpty()) return;

        long selfId = payload.getSelfId();
        WebSocketSession session = sessionRegistry.get(selfId);
        if (session == null) throw new SessionNotExistsException("session not exists! self id: " + selfId);
        if (!session.isOpen()) return;

        try {
            synchronized (session) {
                String actionJson = payload.getMessageType().equals("group") ?
                        actionBuildUtil.buildGroupSendStrAction(payload.getGroupId(), description) :
                        actionBuildUtil.buildPrivateSendStrAction(payload.getUserId(), description);
                session.sendMessage(new TextMessage(actionJson));
                log.debug("send action: {}", actionJson);
            }
        } catch (IOException io) {
            log.error("send WebSocket failed, sid={}", selfId, io);
        }
    }
}