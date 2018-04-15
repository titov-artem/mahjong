package com.github.mahjong.league.repo.jdbc;

import com.github.mahjong.league.model.JoinRequest;
import com.github.mahjong.league.model.JoinRequest.Decision;
import com.github.mahjong.league.repo.JoinRequestRepo;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static com.github.mahjong.league.repo.jdbc.generated.tables.JoinRequest.JOIN_REQUEST;
import static com.github.mahjong.league.repo.jdbc.generated.tables.League.LEAGUE;

@Repository
public class JdbcJoinRequestRepo extends BaseRepo implements JoinRequestRepo {

    private static final RecordMapper<Record, JoinRequest> RECORD_MAPPER = record ->
            new JoinRequest(
                    JOIN_REQUEST.ID.get(record),
                    JOIN_REQUEST.LEAGUE_ID.get(record),
                    JOIN_REQUEST.PLAYER_ID.get(record),
                    JOIN_REQUEST.CREATED_AT.get(record),
                    Decision.valueOf(JOIN_REQUEST.DECISION.get(record)),
                    JOIN_REQUEST.REASON.get(record),
                    JOIN_REQUEST.REVIEWED_BY.get(record),
                    JOIN_REQUEST.REVIEWED_AT.get(record),
                    JOIN_REQUEST.EXPIRE_AT.get(record)
            );

    @Inject
    public JdbcJoinRequestRepo(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<JoinRequest> get(Long id) {
        return dsl().select().from(JOIN_REQUEST)
                .where(JOIN_REQUEST.ID.eq(id))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public List<JoinRequest> getByPlayerId(Long playerId) {
        return dsl().select().from(JOIN_REQUEST).where(JOIN_REQUEST.PLAYER_ID.eq(playerId))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<JoinRequest> getPendingByPlayerId(Long playerId) {
        return dsl().select().from(JOIN_REQUEST).where(JOIN_REQUEST.PLAYER_ID.eq(playerId))
                .and(JOIN_REQUEST.DECISION.eq(Decision.PENDING.name()))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public Optional<JoinRequest> getPendingByPlayerAndLeague(Long playerId, Long leagueId) {
        return dsl().select().from(JOIN_REQUEST)
                .where(JOIN_REQUEST.PLAYER_ID.eq(playerId))
                .and(JOIN_REQUEST.LEAGUE_ID.eq(leagueId))
                .and(JOIN_REQUEST.DECISION.eq(Decision.PENDING.name()))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public List<JoinRequest> getPendingForAdmin(Long leagueId, Long adminId) {
        return dsl().select().from(JOIN_REQUEST)
                .join(LEAGUE).on(JOIN_REQUEST.LEAGUE_ID.eq(LEAGUE.ID))
                .where(JOIN_REQUEST.LEAGUE_ID.eq(leagueId))
                .and(LEAGUE.ADMINS.contains(new Long[]{adminId}))
                .and(JOIN_REQUEST.DECISION.eq(Decision.PENDING.name()))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public JoinRequest create(JoinRequest request) {
        return dsl().insertInto(JOIN_REQUEST)
                .set(JOIN_REQUEST.LEAGUE_ID, request.getLeagueId())
                .set(JOIN_REQUEST.PLAYER_ID, request.getPlayerId())
                .set(JOIN_REQUEST.CREATED_AT, request.getCreatedAt())
                .set(JOIN_REQUEST.DECISION, request.getDecision().name())
                .set(JOIN_REQUEST.REASON, request.getReason())
                .set(JOIN_REQUEST.REVIEWED_BY, request.getReviewedBy())
                .set(JOIN_REQUEST.REVIEWED_AT, request.getReviewedAt())
                .set(JOIN_REQUEST.EXPIRE_AT, request.getExpireAt())
                .returning()
                .fetchOne()
                .map(RECORD_MAPPER);
    }

    @Override
    public JoinRequest update(JoinRequest request) {
        return dsl().update(JOIN_REQUEST)
                .set(JOIN_REQUEST.LEAGUE_ID, request.getLeagueId())
                .set(JOIN_REQUEST.PLAYER_ID, request.getPlayerId())
                .set(JOIN_REQUEST.CREATED_AT, request.getCreatedAt())
                .set(JOIN_REQUEST.DECISION, request.getDecision().name())
                .set(JOIN_REQUEST.REASON, request.getReason())
                .set(JOIN_REQUEST.REVIEWED_BY, request.getReviewedBy())
                .set(JOIN_REQUEST.REVIEWED_AT, request.getReviewedAt())
                .set(JOIN_REQUEST.EXPIRE_AT, request.getExpireAt())
                .where(JOIN_REQUEST.ID.eq(request.getId()))
                .returning()
                .fetchOne()
                .map(RECORD_MAPPER);
    }
}
