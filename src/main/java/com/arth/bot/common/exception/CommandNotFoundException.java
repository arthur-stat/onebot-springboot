package com.arth.bot.common.exception;

public class CommandNotFoundException extends BusinessException {

    public CommandNotFoundException(String message) {
        super(ErrorCode.PERMISSION_DENIED, message.isEmpty() ? "Command Not Found" : message, null);
    }

    public CommandNotFoundException(String message, String description) {
        super(ErrorCode.PERMISSION_DENIED, message.isEmpty() ? "Command Not Found" : message, description);
    }
}
