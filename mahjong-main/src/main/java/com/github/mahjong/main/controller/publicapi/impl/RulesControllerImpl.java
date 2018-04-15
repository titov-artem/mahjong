package com.github.mahjong.main.controller.publicapi.impl;

import com.github.mahjong.application.context.UserContextSupport;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.controller.publicapi.dto.CombinationViewHelper;
import com.github.mahjong.main.controller.publicapi.dto.RulesSetViewHelper;
import com.github.mahjong.main.exceptions.RulesSetNotFoundException;
import com.github.mahjong.main.publicapi.RulesController;
import com.github.mahjong.main.publicapi.dto.CombinationView;
import com.github.mahjong.main.publicapi.dto.RulesSetView;
import com.github.mahjong.main.rules.RulesSet;
import com.github.mahjong.main.rules.RulesSetRegistry;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller("publicRulesController")
public class RulesControllerImpl implements RulesController {

    private final RulesSetRegistry rulesSetRegistry;
    private final UserContextSupport userContextSupport;

    @Inject
    public RulesControllerImpl(RulesSetRegistry rulesSetRegistry, UserContextSupport userContextSupport) {
        this.rulesSetRegistry = rulesSetRegistry;
        this.userContextSupport = userContextSupport;
    }

    @Override
    public List<RulesSetView> getAll() {
        return rulesSetRegistry.getRegistered().stream()
                .map(rs -> RulesSetViewHelper.from(rs, userContextSupport.getUserLang()))
                .collect(toList());
    }

    @Override
    public RulesSetView get(String rulesSetCode) {
        RulesSet rulesSet = rulesSetRegistry.getRulesSet(rulesSetCode)
                .orElseThrow(RulesSetNotFoundException.supplier(rulesSetCode));
        return RulesSetViewHelper.from(rulesSet, userContextSupport.getUserLang());
    }

    @Override
    public List<CombinationView> getAllCombinations(String rulesSetCode) {
        RulesSet rulesSet = rulesSetRegistry.getRulesSet(rulesSetCode)
                .orElseThrow(RulesSetNotFoundException.supplier(rulesSetCode));
        return rulesSet.getAvailableCombinations().stream()
                .map(c -> CombinationViewHelper.from(c, rulesSet, userContextSupport.getUserLang()))
                .collect(toList());
    }

}
