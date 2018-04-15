package com.github.mahjong.common.exceptions;

/**
 * General exception for mahjong server. Used as a base class for all exceptions.
 */
public class InternalInvariantFailedException extends GeneralServerException {


    public InternalInvariantFailedException(String message) {
        super(message, ExceptionClass.INTERNAL_INVARIANT_FAILED_EXCEPTION);
    }

    public InternalInvariantFailedException(String message, Throwable cause) {
        super(message, ExceptionClass.INTERNAL_INVARIANT_FAILED_EXCEPTION, cause);
    }

}
