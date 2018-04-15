package com.github.mahjong.main.privateapi.dto;

import com.github.mahjong.api.common.dto.WindDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameStartForm {

    /**
     * Players, that will participate in the game.
     */
    @NotNull
    @Size(min = 4, max = 4)
    public List<Long> players;
    /**
     * Players, that are seating on specific wind. If some player not specified their, it means it has random wind.
     */
    @NotNull
    @Size(max = 4)
    public Map<WindDto, Long> windToPlayer;
    @NotEmpty
    public String rulesSet;
    @NotEmpty
    public Set<WindDto> windsToPlay;
    public int startPoints;
    public boolean withUma;

}
