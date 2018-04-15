package com.github.mahjong.league.model;

import lombok.Data;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.LocalDateTime;

@Data
public class JoinRequest {

    private final long id;
    private final long leagueId;
    private final long playerId;
    private final LocalDateTime createdAt;
    private final Decision decision;
    @Nullable
    private final String reason;
    @Nullable
    private final Long reviewedBy;
    @Nullable
    private final LocalDateTime reviewedAt;
    private final LocalDateTime expireAt;


    public enum Decision {
        PENDING, APPROVED, REJECTED;
    }

    public static JoinRequest createNew(Long leagueId, Long playerId, LocalDateTime createdAt, LocalDateTime expireAt) {
        return new JoinRequest(-1,
                leagueId,
                playerId,
                createdAt,
                Decision.PENDING,
                null,
                null,
                null,
                expireAt);
    }

    public boolean isExpired(Clock clock) {
        return expireAt.isBefore(LocalDateTime.now(clock));
    }

    public JoinRequest approved(Long reviewerId, Clock clock) {
        return new JoinRequest(
                id,
                leagueId,
                playerId,
                createdAt,
                Decision.APPROVED,
                "",
                reviewerId,
                LocalDateTime.now(clock),
                expireAt
        );
    }

    public JoinRequest rejected(Long reviewerId, String reason, Clock clock) {
        return new JoinRequest(
                id,
                leagueId,
                playerId,
                createdAt,
                Decision.REJECTED,
                reason,
                reviewerId,
                LocalDateTime.now(clock),
                expireAt
        );
    }
}
