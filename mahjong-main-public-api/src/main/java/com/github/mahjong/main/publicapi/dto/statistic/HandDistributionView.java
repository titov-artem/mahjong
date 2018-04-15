package com.github.mahjong.main.publicapi.dto.statistic;

import java.math.BigDecimal;
import java.util.List;

public class HandDistributionView {

    public String rules;
    public HandView bestHand;
    public HandView worstHand;
    public BigDecimal avgPoints;

    public static class HandView {
        public List<String> combinations;
        public int points;
    }

}
