package com.github.mahjong.main.rules;

import java.util.Collection;
import java.util.Optional;

public interface RulesSetRegistry {

    Collection<RulesSet> getRegistered();

    Optional<RulesSet> getRulesSet(String code);

}
