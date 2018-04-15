package com.github.mahjong.main.exceptions;

import com.github.mahjong.common.exceptions.EntityNotFoundException;
import com.github.mahjong.common.translation.EntityType;

import java.util.function.Supplier;

public class PlayerNotFoundException extends EntityNotFoundException {

    public PlayerNotFoundException(Long playerId) {
        super(playerId, EntityType.PLAYER.getEntityTypeCode());
    }

    public static Supplier<PlayerNotFoundException> supplier(Long playerId) {
        return () -> new PlayerNotFoundException(playerId);
    }
}
