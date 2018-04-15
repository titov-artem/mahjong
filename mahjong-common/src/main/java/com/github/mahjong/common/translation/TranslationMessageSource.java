package com.github.mahjong.common.translation;

import com.github.mahjong.common.enums.LangIso639;

public interface TranslationMessageSource {

    String getTranslated(String messageCode, LangIso639 primaryLang, LangIso639 secondaryLang);

}
