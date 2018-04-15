package com.github.mahjong.league.service.model;

import com.github.mahjong.common.enums.LangIso639;
import lombok.Data;

@Data
public class Player {

    private final Long id;
    private final String login;
    private final String name;
    private final LangIso639 lang;

}
