package com.github.mahjong.main.privateapi.dto;

import com.github.mahjong.api.common.LangIso639Dto;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class PlayerDto {

    @Nullable
    public Long id;
    @NotNull
    public String login;
    @NotNull
    public String name;
    @NotNull
    public LangIso639Dto lang;

}
