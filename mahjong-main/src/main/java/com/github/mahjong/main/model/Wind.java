package com.github.mahjong.main.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public enum Wind {
    // IMPORTANT: Do not change order of this values, because otherwise Comparable won't correspond order!
    EAST,
    SOUTH,
    WEST,
    NORTH;

    private static final ImmutableMap<Wind, Wind> next = new ImmutableMap.Builder<Wind, Wind>()
            .put(EAST, SOUTH)
            .put(SOUTH, WEST)
            .put(WEST, NORTH)
            .put(NORTH, EAST)
            .build();

    static {
        for (Wind wind : values()) {
            Preconditions.checkState(next.containsKey(wind), "Next wind not specified for wind %s", wind);
        }
        if (!getOrdered().equals(Arrays.stream(values()).sorted().collect(toList()))) {
            throw new AssertionError("Wind#compareTo doesn't correspond to Wind#getOrdered()");
        }
    }

    public Wind next() {
        return next.get(this);
    }

    public static List<Wind> getOrdered() {
        return Arrays.asList(EAST, SOUTH, WEST, NORTH);
    }


}
