package com.github.mahjong.common.exceptions;

/**
 * General exception for mahjong server. Used as a base class for all exceptions.
 */
public class GeneralServerException extends RuntimeException {

    private final ExceptionClass exceptionClass;

    public GeneralServerException(String message, ExceptionClass exceptionClass) {
        super(message);
        this.exceptionClass = exceptionClass;
    }

    public GeneralServerException(String message, ExceptionClass exceptionClass, Throwable cause) {
        super(message, cause);
        this.exceptionClass = exceptionClass;
    }

    public ExceptionClass getExceptionClass() {
        return exceptionClass;
    }
}
