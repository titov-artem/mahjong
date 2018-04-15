package com.github.mahjong.common.translation;

public enum EntityType {

    GAME("mahjong.game"),
    PLAYER("mahjong.player"),
    RULES_SET("mahjong.rules.set"),
    LEAGUE("mahjong.league"),
    INVITATION("mahjong.league.invitation"),
    JOIN_REQUEST("mahjong.league.join.request"),
    ;

    private final String entityTypeCode;

    EntityType(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public String getEntityTypeCode() {
        return entityTypeCode;
    }
}
