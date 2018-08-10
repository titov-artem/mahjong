package com.github.mahjong.main.publicapi.dto.statistic;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class PlaceDistributionView {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public String rulesSetCode;
    public int gamesPlayed;
    /**
     * Average player's place. Will be null if player played 0 games
     */
    public @Nullable
    BigDecimal avgPlace;
    /**
     * Player's place to its percentage. Map will be null if player played 0 games
     */
    public @Nullable
    Map<Integer, BigDecimal> placePercentage;

    public PlaceDistributionView(String rulesSetCode,
                                 int gamesPlayed,
                                 BigDecimal avgPlace,
                                 Map<Integer, BigDecimal> placePercentage) {
        this.rulesSetCode = rulesSetCode;
        this.gamesPlayed = gamesPlayed;
        this.avgPlace = avgPlace;
        this.placePercentage = placePercentage;
    }

    public static PlaceDistributionView from(String rulesSetCode, Map<Integer, Integer> placesCount) {
        if (placesCount.isEmpty()) {
            return new PlaceDistributionView(rulesSetCode, 0, null, null);
        }
        int gamesPlayed = 0;
        int summedPlace = 0;
        int maxPlace = -1;
        for (Map.Entry<Integer, Integer> entry : placesCount.entrySet()) {
            gamesPlayed += entry.getValue();
            summedPlace += entry.getKey();
            if (entry.getKey() > maxPlace) {
                maxPlace = entry.getKey();
            }
        }
        BigDecimal gamesPlayedBD = BigDecimal.valueOf(gamesPlayed);
        Map<Integer, BigDecimal> placePercentage = new HashMap<>();
        placesCount.forEach((place, count) -> {
            placePercentage.put(
                    place,
                    BigDecimal.valueOf(count).divide(gamesPlayedBD, 2, RoundingMode.HALF_UP).multiply(HUNDRED)
            );
        });
        return new PlaceDistributionView(
                rulesSetCode,
                gamesPlayed,
                BigDecimal.valueOf(summedPlace).divide(gamesPlayedBD, 2, RoundingMode.HALF_UP),
                placePercentage
        );
    }
}
