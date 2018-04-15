package com.github.mahjong.main.publicapi.dto.statistic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CombinationDistributionView {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public String rulesSetCode;
    public int total;
    public Map<String, BigDecimal> combinationsPercentage;

    public static CombinationDistributionView from(String rulesSetCode,
                                                   Iterable<String> combinationCodes) {
        CombinationDistributionView view = new CombinationDistributionView();
        view.rulesSetCode = rulesSetCode;
        view.total = 0;
        view.combinationsPercentage = new HashMap<>();
        if (!combinationCodes.iterator().hasNext()) {
            return view;
        }
        Map<String, Integer> combinationsCount = new HashMap<>();
        for (String combination : combinationCodes) {
            Integer cur = combinationsCount.putIfAbsent(combination, 0);
            combinationsCount.put(combination, (cur == null ? 0 : cur) + 1);
            view.total++;
        }
        BigDecimal totalBD = BigDecimal.valueOf(view.total);
        combinationsCount.forEach((combination, count) -> {
            view.combinationsPercentage.put(
                    combination,
                    BigDecimal.valueOf(count).divide(totalBD, 2, BigDecimal.ROUND_HALF_UP).multiply(HUNDRED)
            );
        });
        return view;
    }

}
