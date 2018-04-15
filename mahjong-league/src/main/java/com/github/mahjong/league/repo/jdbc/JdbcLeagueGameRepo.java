package com.github.mahjong.league.repo.jdbc;

import com.github.mahjong.league.model.LeagueGame;
import com.github.mahjong.league.repo.LeagueGameRepo;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static com.github.mahjong.league.repo.jdbc.generated.tables.LeagueGame.LEAGUE_GAME;

@Repository
public class JdbcLeagueGameRepo extends BaseRepo implements LeagueGameRepo {

    private static final RecordMapper<Record, LeagueGame> RECORD_MAPPER = record ->
            new LeagueGame(
                    LEAGUE_GAME.LEAGUE_ID.get(record),
                    LEAGUE_GAME.GAME_ID.get(record),
                    Arrays.asList(LEAGUE_GAME.PLAYER_IDS.get(record))
            );

    @Inject
    public JdbcLeagueGameRepo(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public LeagueGame create(LeagueGame leagueGame) {
        dsl().insertInto(LEAGUE_GAME)
                .set(LEAGUE_GAME.LEAGUE_ID, leagueGame.getLeagueId())
                .set(LEAGUE_GAME.GAME_ID, leagueGame.getGameId())
                .set(LEAGUE_GAME.PLAYER_IDS, leagueGame.getPlayerIds().toArray(new Long[leagueGame.getPlayerIds().size()]))
                .execute();
        return leagueGame;
    }

    @Override
    public List<LeagueGame> getAllWithPlayer(long leagueId, long playerId) {
        return dsl().select().from(LEAGUE_GAME).where(LEAGUE_GAME.LEAGUE_ID.eq(leagueId))
                .and(LEAGUE_GAME.PLAYER_IDS.contains(new Long[]{playerId}))
                .fetch()
                .map(RECORD_MAPPER);
    }
}
