package com.arth.bot.core.common.exception.aop;

import com.arth.bot.adapter.sender.Sender;
import com.arth.bot.adapter.session.SessionRegistry;
import com.arth.bot.core.common.dto.ParsedPayloadDTO;
import com.arth.bot.core.common.exception.BusinessException;
import com.arth.bot.core.common.exception.CommandNotFoundException;
import com.arth.bot.core.common.exception.InvalidCommandArgsException;
import com.arth.bot.core.common.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * BusinessException 的异常拦截器，包含可能的由 bot 向用户主动通知可读错误信息的逻辑
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BusinessExceptionHandlerAspect {

    private final SessionRegistry sessionRegistry;
    private final Sender sender;

    /**
     * 切点范围为以参数名 payload 作为首个参数的 service 下的所有类的全部方法
     * @param joinPoint
     * @param payload
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.arth.bot.core.routing.CommandInvoker.parseAndInvoke(..)) && args(payload)")
    public Object around(ProceedingJoinPoint joinPoint, ParsedPayloadDTO payload) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (PermissionDeniedException e) {
            log.debug("[core: business exception] permission denied: {}", e.getMessage());
            sendBusinessExceptionBack(e, payload);
            return null;
        } catch (InvalidCommandArgsException e) {
            log.debug("[core: business exception] invalid command args: {}", e.getMessage());
            sendBusinessExceptionBack(e, payload);
            return null;
        } catch (CommandNotFoundException e) {
            log.debug("[core: business exception] command not found: {}", e.getMessage());
            sendBusinessExceptionBack(e, payload);
            return null;
        } catch (BusinessException e) {
            log.warn("[core: business exception] business error: {}", e.getMessage(), e);
            return null;
        }
    }

    private void sendBusinessExceptionBack(BusinessException e, ParsedPayloadDTO payload) {
        String description = e.getDescription();
        if (description == null || description.isBlank()) return;

        sender.sendText(payload, description);
    }
}