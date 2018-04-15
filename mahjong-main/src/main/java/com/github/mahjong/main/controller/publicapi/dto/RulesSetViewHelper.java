package com.github.mahjong.main.controller.publicapi.dto;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.publicapi.dto.CombinationsGroupView;
import com.github.mahjong.main.publicapi.dto.CombinationsView;
import com.github.mahjong.main.publicapi.dto.RulesSetView;
import com.github.mahjong.main.rules.Combination;
import com.github.mahjong.main.rules.RulesSet;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class RulesSetViewHelper {

    public static RulesSetView from(RulesSet rulesSet, LangIso639 lang) {
        RulesSetView view = new RulesSetView();
        view.code = rulesSet.getCode();
        view.name = rulesSet.getName(lang);
        view.description = rulesSet.getDescription(lang);
        view.combinations = constructCombinationsView(rulesSet, lang);
        return view;
    }

    private static CombinationsView constructCombinationsView(RulesSet rulesSet, LangIso639 lang) {
        Map<Integer, List<Combination>> groupedCombinations = new HashMap<>();
        rulesSet.getAvailableCombinations().forEach(combination -> {
            groupedCombinations.putIfAbsent(combination.getGroup(), new ArrayList<>());
            groupedCombinations.get(combination.getGroup()).add(combination);
        });
        List<CombinationsGroupView> groups = groupedCombinations.entrySet().stream()
                .map(enrty -> {
                    CombinationsGroupView groupView = new CombinationsGroupView();
                    groupView.order = enrty.getKey();
                    groupView.combinations = enrty.getValue().stream()
                            .sorted(Comparator.comparingInt(Combination::getOrder))
                            .map(c -> CombinationViewHelper.from(c, rulesSet, lang))
                            .collect(toList());
                    return groupView;
                })
                .sorted(Comparator.comparingInt(gV -> gV.order))
                .collect(toList());
        CombinationsView combs = new CombinationsView();
        combs.groups = groups;
        return combs;
    }

}
