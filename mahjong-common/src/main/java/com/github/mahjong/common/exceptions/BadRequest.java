package com.github.mahjong.common.exceptions;

public class BadRequest extends GeneralTranslatableServerException {

    public BadRequest(String messageCode) {
        super(messageCode, ExceptionClass.BAD_REQUEST_EXCEPTION);
    }

    public BadRequest(String messageCode, Throwable cause) {
        super(messageCode, ExceptionClass.BAD_REQUEST_EXCEPTION, cause);
    }

}
