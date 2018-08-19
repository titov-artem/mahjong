package com.github.mahjong.main.service

import com.github.mahjong.main.rules.RulesSet
import com.github.mahjong.main.rules.RulesSetRegistry
import com.github.mahjong.main.rules.riichi.ema.RiichiEMARuleSet

class RiichiEmaRulesSetRegistry implements RulesSetRegistry {

    private final RulesSet riichiEmaRulesSet = new RiichiEMARuleSet()

    @Override
    Collection<RulesSet> getRegistered() {
        return [riichiEmaRulesSet]
    }

    @Override
    Optional<RulesSet> getRulesSet(String code) {
        if (!Objects.equals(code, riichiEmaRulesSet.getCode())) {
            return Optional.empty()
        }
        return Optional.of(riichiEmaRulesSet)
    }
}
