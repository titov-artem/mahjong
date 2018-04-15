package com.github.mahjong.common.exceptions;

public class PreconditionFailed extends GeneralTranslatableServerException {

    public PreconditionFailed(String messageCode) {
        super(messageCode, ExceptionClass.PRECONDITION_FAILED_EXCEPTION);
    }

    public PreconditionFailed(String messageCode, Throwable cause) {
        super(messageCode, ExceptionClass.PRECONDITION_FAILED_EXCEPTION, cause);
    }

}
