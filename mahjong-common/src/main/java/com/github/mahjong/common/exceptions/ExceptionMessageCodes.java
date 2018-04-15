package com.github.mahjong.common.exceptions;

public enum ExceptionMessageCodes {

    ENTITY_NOT_FOUND("mahjong.exceptions.entity.not.found"),
    ;

    private final String messageCode;

    ExceptionMessageCodes(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
