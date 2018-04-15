package com.github.mahjong.main.controller.publicapi.dto;


import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.publicapi.dto.CombinationView;
import com.github.mahjong.main.rules.Combination;
import com.github.mahjong.main.rules.RulesSet;

public class CombinationViewHelper {

    public static CombinationView from(Combination combination, RulesSet rulesSet, LangIso639 lang) {
        CombinationView view = new CombinationView();
        view.code = combination.getCode();
        view.name = rulesSet.getCombinationName(combination, lang);
        view.openPrice = combination.getScore(true);
        view.closePrice = combination.getScore(false);
        return view;
    }

}
