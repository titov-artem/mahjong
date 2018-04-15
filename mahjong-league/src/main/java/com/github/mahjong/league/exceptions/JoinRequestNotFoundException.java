package com.github.mahjong.league.exceptions;

import com.github.mahjong.common.exceptions.EntityNotFoundException;
import com.github.mahjong.common.translation.EntityType;

import java.util.function.Supplier;

public class JoinRequestNotFoundException extends EntityNotFoundException {

    public JoinRequestNotFoundException(Long id) {
        super(id, EntityType.JOIN_REQUEST.getEntityTypeCode());
    }

    public static Supplier<JoinRequestNotFoundException> supplier(Long id) {
        return () -> new JoinRequestNotFoundException(id);
    }
}
