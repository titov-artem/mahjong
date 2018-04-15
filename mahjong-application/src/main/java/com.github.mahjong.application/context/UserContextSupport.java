package com.github.mahjong.application.context;

import com.github.mahjong.common.enums.LangIso639;

/**
 * Provide data about user operating in current thread local context.
 */
public interface UserContextSupport {

    LangIso639 getUserLang();

}
