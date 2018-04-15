package com.github.mahjong.league.repo.jdbc;

import com.github.mahjong.league.model.LeaguePlayer;
import com.github.mahjong.league.repo.LeaguePlayerRepo;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.mahjong.league.repo.jdbc.generated.tables.LeaguePlayer.LEAGUE_PLAYER;

@Repository
public class JdbcLeaguePlayerRepo extends BaseRepo implements LeaguePlayerRepo {

    private static final RecordMapper<Record, LeaguePlayer> RECORD_MAPPER = record ->
            new LeaguePlayer(
                    LEAGUE_PLAYER.LEAGUE_ID.get(record),
                    LEAGUE_PLAYER.PLAYER_ID.get(record)
            );

    @Inject
    public JdbcLeaguePlayerRepo(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<LeaguePlayer> get(Long leagueId, Long playerId) {
        return dsl().select().from(LEAGUE_PLAYER).where(LEAGUE_PLAYER.LEAGUE_ID.eq(leagueId))
                .and(LEAGUE_PLAYER.PLAYER_ID.eq(playerId))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public List<LeaguePlayer> getAllByLeague(Long leagueId) {
        return dsl().select().from(LEAGUE_PLAYER).where(LEAGUE_PLAYER.LEAGUE_ID.eq(leagueId))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<LeaguePlayer> getAllByLeague(Long leagueId, Set<Long> playerIds) {
        return dsl().select().from(LEAGUE_PLAYER).where(LEAGUE_PLAYER.LEAGUE_ID.eq(leagueId))
                .and(LEAGUE_PLAYER.PLAYER_ID.in(playerIds))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<LeaguePlayer> getAllByPlayer(Long playerId) {
        return dsl().select().from(LEAGUE_PLAYER).where(LEAGUE_PLAYER.PLAYER_ID.eq(playerId))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public LeaguePlayer create(LeaguePlayer leaguePlayer) {
        dsl().insertInto(LEAGUE_PLAYER)
                .set(LEAGUE_PLAYER.LEAGUE_ID, leaguePlayer.getLeagueId())
                .set(LEAGUE_PLAYER.PLAYER_ID, leaguePlayer.getPlayerId())
                .execute();
        return leaguePlayer;
    }
}
