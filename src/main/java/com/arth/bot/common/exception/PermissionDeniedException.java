package com.arth.bot.common.exception;

public class PermissionDeniedException extends BusinessException {

    public PermissionDeniedException(String message) {
        super(ErrorCode.PERMISSION_DENIED, message.isEmpty() ? "Permission Denied" : message, null);
    }

    public PermissionDeniedException(String message, String description) {
        super(ErrorCode.PERMISSION_DENIED, message.isEmpty() ? "Permission Denied" : message, description);
    }
}
