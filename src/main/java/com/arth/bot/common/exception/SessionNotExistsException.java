package com.arth.bot.common.exception;

public class SessionNotExistsException extends BusinessException {

    public SessionNotExistsException(String message) {
        super(ErrorCode.PERMISSION_DENIED, message.isEmpty() ? "Session Not Exists" : message, null);
    }

    public SessionNotExistsException(String message, String description) {
        super(ErrorCode.PERMISSION_DENIED, message.isEmpty() ? "Session Not Exists" : message, description);
    }
}
