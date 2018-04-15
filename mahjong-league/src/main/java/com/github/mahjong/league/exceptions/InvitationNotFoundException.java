package com.github.mahjong.league.exceptions;

import com.github.mahjong.common.exceptions.EntityNotFoundException;
import com.github.mahjong.common.translation.EntityType;

import java.util.function.Supplier;

public class InvitationNotFoundException extends EntityNotFoundException {

    public InvitationNotFoundException(String code) {
        super(code, EntityType.INVITATION.getEntityTypeCode());
    }

    public static Supplier<InvitationNotFoundException> supplier(String code) {
        return () -> new InvitationNotFoundException(code);
    }
}
