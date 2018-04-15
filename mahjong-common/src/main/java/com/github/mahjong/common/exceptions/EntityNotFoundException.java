package com.github.mahjong.common.exceptions;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.common.translation.TranslationMessageSource;

import java.util.function.Supplier;

/**
 * Base exception class for entity not found exceptions.
 */
public class EntityNotFoundException extends GeneralTranslatableServerException {

    private final Object entityId;
    private final String entityTypeCode;

    public EntityNotFoundException(Object entityId, String entityTypeCode) {
        super(ExceptionMessageCodes.ENTITY_NOT_FOUND.getMessageCode(), ExceptionClass.NOT_FOUND_EXCEPTION);
        this.entityId = entityId;
        this.entityTypeCode = entityTypeCode;
    }

    @Override
    public String translate(TranslationMessageSource messageSource, LangIso639 primaryLang, LangIso639 secondaryLang) {
        return String.format(
                super.translate(messageSource, primaryLang, secondaryLang),
                messageSource.getTranslated(entityTypeCode, primaryLang, secondaryLang),
                entityId
        );
    }
}
