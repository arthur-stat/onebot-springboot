package com.arth.bot.common.exception;

public class InvalidCommandArgsException extends BusinessException {

    public InvalidCommandArgsException(String message) {
         super(ErrorCode.INVALID_COMMAND_ARGS, message.isEmpty() ? "Invalid Command Args" : message, null);
    }

    public InvalidCommandArgsException(String message, String description) {
        super(ErrorCode.INVALID_COMMAND_ARGS, message.isEmpty() ? "Invalid Command Args" : message, description);
    }
}
