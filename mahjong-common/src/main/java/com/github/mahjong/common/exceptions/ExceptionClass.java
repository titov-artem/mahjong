package com.github.mahjong.common.exceptions;

import javax.ws.rs.core.Response;

public enum ExceptionClass {

    /* 4xx */
    NOT_FOUND_EXCEPTION(Response.Status.NOT_FOUND),
    BAD_REQUEST_EXCEPTION(Response.Status.BAD_REQUEST),
    PRECONDITION_FAILED_EXCEPTION(Response.Status.PRECONDITION_FAILED),

    /* 5xx */
    UNKNOWN(Response.Status.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_EXCEPTION(Response.Status.INTERNAL_SERVER_ERROR),
    INTERNAL_INVARIANT_FAILED_EXCEPTION(Response.Status.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE_EXCEPTION(Response.Status.SERVICE_UNAVAILABLE);

    private Response.Status status;

    ExceptionClass(Response.Status status) {
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
