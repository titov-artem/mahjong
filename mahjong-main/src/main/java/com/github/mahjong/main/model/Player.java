package com.github.mahjong.main.model;

import com.github.mahjong.common.enums.LangIso639;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class Player {

    private final long id;
    private final String login;
    private final String name;
    private final LangIso639 lang;

}
