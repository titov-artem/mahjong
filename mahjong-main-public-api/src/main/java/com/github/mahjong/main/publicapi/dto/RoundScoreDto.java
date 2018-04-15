package com.github.mahjong.main.publicapi.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public class RoundScoreDto {

    @NotNull
    @Size(min = 4, max = 4)
    @Valid
    public List<PlayerScoreDto> scores;
    @NotNull
    public Set<Long> winners;
    @NotNull
    public Set<Long> losers;

}
