package com.github.mahjong.league.exceptions;

import com.github.mahjong.common.exceptions.EntityNotFoundException;
import com.github.mahjong.common.translation.EntityType;

import java.util.function.Supplier;

public class LeagueNotFoundException extends EntityNotFoundException {

    public LeagueNotFoundException(Long id) {
        super(id, EntityType.LEAGUE.getEntityTypeCode());
    }

    public static Supplier<LeagueNotFoundException> supplier(Long id) {
        return () -> new LeagueNotFoundException(id);
    }
}
