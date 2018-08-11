package com.github.mahjong.league.model;

import lombok.Data;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
public class Invitation {

    private final long id;
    private final long leagueId;
    private final long playerId;
    private final String code;
    private final long createdBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime expireAt;
    private final Status status;

    public boolean isExpired(Clock clock) {
        return status == Status.EXPIRED || expireAt.isBefore(LocalDateTime.now(clock));
    }

    public static Invitation createNew(long leagueId,
                                       long playerId,
                                       String code,
                                       long createdBy,
                                       LocalDateTime createdAt,
                                       LocalDateTime expireAt) {
        return new Invitation(-1, leagueId, playerId, code, createdBy, createdAt, expireAt, Status.ACTIVE);
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public enum Status {
        // todo add job to mark expired invitations with status EXPIRED
        ACTIVE, ACCEPTED, REJECTED, EXPIRED
    }
}
