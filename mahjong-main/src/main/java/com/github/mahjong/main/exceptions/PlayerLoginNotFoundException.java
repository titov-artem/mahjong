package com.github.mahjong.main.exceptions;

import com.github.mahjong.common.exceptions.EntityNotFoundException;
import com.github.mahjong.common.translation.EntityType;

import java.util.function.Supplier;

public class PlayerLoginNotFoundException extends EntityNotFoundException {

    public PlayerLoginNotFoundException(String login) {
        super(login, EntityType.PLAYER.getEntityTypeCode());
    }

    public static Supplier<PlayerLoginNotFoundException> supplier(String login) {
        return () -> new PlayerLoginNotFoundException(login);
    }
}
