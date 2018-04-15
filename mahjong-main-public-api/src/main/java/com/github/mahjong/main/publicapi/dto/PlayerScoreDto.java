package com.github.mahjong.main.publicapi.dto;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PlayerScoreDto {

    public long playerId;
    @NotNull
    public List<String> combinationCodes = new ArrayList<>();
    public int doraCount;
    public int fuCount;
    public boolean openHand;
    public boolean riichi;
    public boolean tempai;
}
