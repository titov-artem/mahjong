package com.github.mahjong.application.context;

import com.github.mahjong.common.enums.LangIso639;

public class StaticUserContextSupport implements UserContextSupport {

    @Override
    public LangIso639 getUserLang() {
        return LangIso639.EN;
    }

}
