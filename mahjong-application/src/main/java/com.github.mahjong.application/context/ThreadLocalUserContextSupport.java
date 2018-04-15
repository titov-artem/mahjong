package com.github.mahjong.application.context;

import com.github.mahjong.common.enums.LangIso639;

public class ThreadLocalUserContextSupport implements UserContextSupport {

    private ThreadLocal<LangIso639> localLang = ThreadLocal.withInitial(() -> LangIso639.EN);

    @Override
    public LangIso639 getUserLang() {
        return localLang.get();
    }

    public void initUserLang(LangIso639 lang) {
        localLang.set(lang);
    }
}
