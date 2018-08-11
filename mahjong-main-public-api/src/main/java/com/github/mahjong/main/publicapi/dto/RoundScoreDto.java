package com.github.mahjong.main.publicapi.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

// Fields will be initialized by jax.rs framework during request deserialization
@SuppressWarnings("NullableProblems")
public class RoundScoreDto {

    @NotNull
    @Size(min = 4, max = 4)
    @Valid
    public List<PlayerScoreDto> scores;
    /**
     * Set of players who will receive points in this round
     */
    @NotNull
    public Set<Long> winners;
    /**
     * Set of players who will pay points in this round.
     * For tsumo - all players, except winner will be among losers
     */
    @NotNull
    public Set<Long> losers;

}
