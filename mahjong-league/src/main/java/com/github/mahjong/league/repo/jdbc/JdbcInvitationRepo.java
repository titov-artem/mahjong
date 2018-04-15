package com.github.mahjong.league.repo.jdbc;

import com.github.mahjong.league.model.Invitation;
import com.github.mahjong.league.repo.InvitationRepo;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.github.mahjong.league.repo.jdbc.generated.tables.Invitation.INVITATION;

@Repository
public class JdbcInvitationRepo extends BaseRepo implements InvitationRepo {

    private static final RecordMapper<Record, Invitation> RECORD_MAPPER = record ->
            new Invitation(
                    INVITATION.ID.get(record),
                    INVITATION.LEAGUE_ID.get(record),
                    INVITATION.PLAYER_ID.get(record),
                    INVITATION.CODE.get(record),
                    INVITATION.CREATED_BY.get(record),
                    INVITATION.CREATED_AT.get(record),
                    INVITATION.EXPIRE_AT.get(record),
                    Invitation.Status.valueOf(INVITATION.STATUS.get(record))
            );

    @Inject
    public JdbcInvitationRepo(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Invitation> get(Long id) {
        return dsl().select().from(INVITATION).where(INVITATION.ID.eq(id))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public List<Invitation> getAllActive(Long playerId) {
        return dsl().select().from(INVITATION).where(INVITATION.PLAYER_ID.eq(playerId))
                .and(INVITATION.STATUS.eq(Invitation.Status.ACTIVE.name()))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public Optional<Invitation> getActiveByPlayerAndLeague(Long playerId, Long leagueId) {
        return dsl().select().from(INVITATION).where(INVITATION.PLAYER_ID.eq(playerId))
                .and(INVITATION.LEAGUE_ID.eq(leagueId))
                .and(INVITATION.STATUS.eq(Invitation.Status.ACTIVE.name()))
                .fetchOptional(RECORD_MAPPER);

    }

    @Override
    public Optional<Invitation> getActiveByPlayerAndLeagueForUpdate(Long playerId, Long leagueId) {
        return dsl().select().from(INVITATION).where(INVITATION.PLAYER_ID.eq(playerId))
                .and(INVITATION.LEAGUE_ID.eq(leagueId))
                .and(INVITATION.STATUS.eq(Invitation.Status.ACTIVE.name()))
                .forUpdate()
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public Optional<Invitation> getActiveByPlayerAndCode(Long playerId, String code) {
        return dsl().select().from(INVITATION).where(INVITATION.PLAYER_ID.eq(playerId))
                .and(INVITATION.CODE.eq(code))
                .and(INVITATION.STATUS.eq(Invitation.Status.ACTIVE.name()))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public Invitation create(Invitation invitation) {
        return dsl().insertInto(INVITATION)
                .set(INVITATION.LEAGUE_ID, invitation.getLeagueId())
                .set(INVITATION.PLAYER_ID, invitation.getPlayerId())
                .set(INVITATION.CODE, invitation.getCode())
                .set(INVITATION.CREATED_BY, invitation.getCreatedBy())
                .set(INVITATION.CREATED_AT, invitation.getCreatedAt())
                .set(INVITATION.EXPIRE_AT, invitation.getExpireAt())
                .set(INVITATION.STATUS, invitation.getStatus().name())
                .returning()
                .fetchOne()
                .map(RECORD_MAPPER);
    }

    @Override
    public void prolongActive(Long id, Long authorId, LocalDateTime expireAt) {
        dsl().update(INVITATION)
                .set(INVITATION.CREATED_BY, authorId)
                .set(INVITATION.EXPIRE_AT, expireAt)
                .where(INVITATION.ID.eq(id))
                .and(INVITATION.STATUS.eq(Invitation.Status.ACTIVE.name()))
                .execute();;

    }

    @Override
    public void changeStatus(Long id, Invitation.Status status) {
        dsl().update(INVITATION)
                .set(INVITATION.STATUS, status.name())
                .where(INVITATION.ID.eq(id))
                .execute();
    }
}
