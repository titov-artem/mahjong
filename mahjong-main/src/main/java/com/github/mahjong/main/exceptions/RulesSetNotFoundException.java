package com.github.mahjong.main.exceptions;

import com.github.mahjong.common.exceptions.EntityNotFoundException;
import com.github.mahjong.common.translation.EntityType;

import java.util.function.Supplier;

public class RulesSetNotFoundException extends EntityNotFoundException {

    public RulesSetNotFoundException(String rulesSetCode) {
        super(rulesSetCode, EntityType.RULES_SET.getEntityTypeCode());
    }

    public static Supplier<RulesSetNotFoundException> supplier(String rulesSetCode) {
        return () -> new RulesSetNotFoundException(rulesSetCode);
    }
}
