package com.github.mahjong.main.exceptions;

import com.github.mahjong.common.exceptions.EntityNotFoundException;
import com.github.mahjong.common.translation.EntityType;

import java.util.function.Supplier;

public class GameNotFoundException extends EntityNotFoundException {

    public GameNotFoundException(Long gameId) {
        super(gameId, EntityType.GAME.getEntityTypeCode());
    }

    public static Supplier<GameNotFoundException> supplier(Long gameId) {
        return () -> new GameNotFoundException(gameId);
    }
}
