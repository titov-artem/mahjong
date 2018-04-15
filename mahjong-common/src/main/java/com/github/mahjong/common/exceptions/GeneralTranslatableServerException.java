package com.github.mahjong.common.exceptions;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.common.translation.TranslationMessageSource;

/**
 * Base class for all translatable exceptions
 */
public abstract class GeneralTranslatableServerException extends GeneralServerException {

    public GeneralTranslatableServerException(String messageCode, ExceptionClass exceptionClass) {
        super(messageCode, exceptionClass);
    }

    public GeneralTranslatableServerException(String messageCode, ExceptionClass exceptionClass, Throwable cause) {
        super(messageCode, exceptionClass, cause);
    }

    /**
     * @return message code to use in translation
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public String translate(TranslationMessageSource messageSource, LangIso639 primaryLang, LangIso639 secondaryLang) {
        return messageSource.getTranslated(getMessage(), primaryLang, secondaryLang);
    }
}
