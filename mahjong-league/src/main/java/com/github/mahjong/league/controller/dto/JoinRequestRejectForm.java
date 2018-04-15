package com.github.mahjong.league.controller.dto;

import javax.validation.constraints.NotNull;

public class JoinRequestRejectForm {

    public long id;
    @NotNull
    public String reason = "";

}
