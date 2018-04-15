package com.github.mahjong.application.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.github.mahjong.application.context.UserContextSupport;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.common.exceptions.ExceptionClass;
import com.github.mahjong.common.exceptions.GeneralServerException;
import com.github.mahjong.common.exceptions.GeneralTranslatableServerException;
import com.github.mahjong.common.json.JsonUtil;
import com.github.mahjong.common.translation.TranslationMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ServerExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger log = LoggerFactory.getLogger(ServerExceptionMapper.class);

    private JsonParseExceptionMapper jsonParseExceptionMapper = new JsonParseExceptionMapper();
    private JsonMappingExceptionMapper jsonMappingExceptionMapper = new JsonMappingExceptionMapper();

    private final TranslationMessageSource messageSource;
    private final UserContextSupport userContextSupport;

    public ServerExceptionMapper(TranslationMessageSource messageSource,
                                 UserContextSupport userContextSupport) {
        this.messageSource = messageSource;
        this.userContextSupport = userContextSupport;
    }

    @Override
    public Response toResponse(Exception exception) {
        log.error("", exception);
        if (exception instanceof JsonParseException) {
            return jsonParseExceptionMapper.toResponse((JsonParseException) exception);
        } else if (exception instanceof JsonMappingException) {
            return jsonMappingExceptionMapper.toResponse((JsonMappingException) exception);
        } else if (exception instanceof WebApplicationException) {
            return mapWebApplicationException((WebApplicationException) exception);
        } else if (exception instanceof GeneralServerException) {
            return mapGeneralServerException((GeneralServerException) exception);
        }
        log.error("UNKNOWN EXCEPTION", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }

    private Response mapWebApplicationException(WebApplicationException exception) {
        return Response.status(exception.getResponse().getStatus()).build();
    }

    private Response mapGeneralServerException(GeneralServerException exception) {
        if (exception instanceof GeneralTranslatableServerException) {
            return mapTranslatableException((GeneralTranslatableServerException) exception);
        }
        log.error("UNKNOWN GENERAL SERVER EXCEPTION", exception);
        return Response.status(exception.getExceptionClass().getStatus())
                .build();
    }

    private Response mapTranslatableException(GeneralTranslatableServerException exception) {
        return Response.status(exception.getExceptionClass().getStatus())
                .entity(
                        serializeEntity(
                                ExceptionResponse.of(
                                        exception.getExceptionClass(),
                                        exception.translate(
                                                messageSource,
                                                userContextSupport.getUserLang(),
                                                LangIso639.EN
                                        )
                                )
                        )
                )
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    // it is forbidden to throw exception from here
    private String serializeEntity(ExceptionResponse resp) {
        try {
            return JsonUtil.writeValue(resp).toString();
        } catch (RuntimeException e) {
            log.error("Failed to serialize full exception response. Using dummy fallback.", e);
            // dummy fallback serialization
            return String.format("{\"cls\": \"%s\", \"message\": \"%s\"}", resp.cls, resp.message);
        }
    }

    public static class ExceptionResponse {
        public ExceptionClass cls;
        public String message;
        public Object payload;

        public static ExceptionResponse fromUnknown(Exception e) {
            return of(ExceptionClass.UNKNOWN, e.getMessage(), null);
        }

        public static ExceptionResponse of(ExceptionClass cl, String message) {
            return of(cl, message, null);
        }

        public static ExceptionResponse of(ExceptionClass exceptionClass, String message, Object payload) {
            ExceptionResponse out = new ExceptionResponse();
            out.cls = exceptionClass;
            out.message = message;
            out.payload = payload;
            return out;
        }
    }
}
