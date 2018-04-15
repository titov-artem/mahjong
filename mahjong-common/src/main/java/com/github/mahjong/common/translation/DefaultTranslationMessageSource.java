package com.github.mahjong.common.translation;

import com.github.mahjong.common.enums.LangIso639;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Properties;

public class DefaultTranslationMessageSource implements TranslationMessageSource {
    private static final Logger log = LoggerFactory.getLogger(DefaultTranslationMessageSource.class);

    private final MessageSource resourceBundleMessageSource;

    public DefaultTranslationMessageSource(String... resourceBundleLocations) {
        resourceBundleMessageSource = buildMessageSource(resourceBundleLocations);
    }

    private MessageSource buildMessageSource(String... resourceBundleLocations) {
        ReloadableResourceBundleMessageSource out = new ReloadableResourceBundleMessageSource();
        out.setBasenames(resourceBundleLocations);
        Properties encoding = new Properties();
        for (String location : resourceBundleLocations) {
            encoding.setProperty(location, "UTF-8");
        }
        out.setFileEncodings(encoding);
        out.setDefaultEncoding("UTF-8");
        return out;
    }

    @Override
    public String getTranslated(String messageCode, LangIso639 primaryLang, LangIso639 secondaryLang) {
        try {
            return this.resourceBundleMessageSource.getMessage(
                    messageCode,
                    new Object[0],
                    new Locale(primaryLang.name().toLowerCase())
            );
        } catch (NoSuchMessageException ignore) {
            log.warn("Missed translation for code {} to lang {}", messageCode, primaryLang);
            return this.resourceBundleMessageSource.getMessage(
                    messageCode,
                    new Object[0],
                    messageCode,
                    new Locale(secondaryLang.name().toLowerCase())
            );
        }
    }
}
