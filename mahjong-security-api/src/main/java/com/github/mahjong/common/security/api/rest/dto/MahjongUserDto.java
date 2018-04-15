package com.github.mahjong.common.security.api.rest.dto;

import javax.validation.constraints.NotEmpty;

public class MahjongUserDto {

    @NotEmpty
    public String login;
    @NotEmpty
    public String password;

}
