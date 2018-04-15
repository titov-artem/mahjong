package com.github.mahjong.main.repo.jdbc;

import com.github.mahjong.common.json.JsonUtil;
import com.github.mahjong.main.model.Game;
import com.github.mahjong.main.model.GameData;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.repo.GameRepo;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.github.mahjong.main.repo.jdbc.generated.tables.Game.GAME;

@Repository
public class JdbcGameRepo extends BaseRepo implements GameRepo {

    private static final RecordMapper<Record, Game> RECORD_MAPPER = record -> {
        try {
            return new Game(
                    GAME.ID.get(record),
                    Arrays.asList(GAME.PLAYER_IDS.get(record)),
                    JsonUtil.readValue(GAME.GAME_DATA.get(record), GameData.class),
                    Arrays.asList(GAME.FINAL_SCORE.get(record)),
                    GAME.IS_COMPLETED.get(record)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse json game data", e);
        }
    };


    @Inject
    public JdbcGameRepo(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Game create(Game game) {
        Long id = dsl().insertInto(GAME)
                .set(GAME.PLAYER_IDS, game.getPlayerIds().toArray(new Long[game.getPlayerIds().size()]))
                .set(GAME.GAME_DATA, JsonUtil.writeValue(game.getGameData()))
                .set(GAME.FINAL_SCORE, game.getFinalScore().toArray(new Integer[game.getFinalScore().size()]))
                .set(GAME.IS_COMPLETED, game.isCompleted())
                .returning(GAME.ID)
                .fetchOne().getId();
        return new Game(id, game.getPlayerIds(), game.getGameData(), game.getFinalScore(), game.isCompleted());
    }

    @Override
    public Optional<Game> get(Long id) {
        return dsl().select().from(GAME).where(GAME.ID.eq(id))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public List<Game> getAll(Collection<Long> ids) {
        return dsl().select().from(GAME).where(GAME.ID.in(ids))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<Game> getAllByPlayer(Player player) {
        return dsl().select().from(GAME)
                .where(GAME.PLAYER_IDS.contains(new Long[]{player.getId()}))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<Game> getActiveByPlayer(Player player) {
        return dsl().select().from(GAME)
                .where(GAME.PLAYER_IDS.contains(new Long[]{player.getId()}))
                .and(GAME.IS_COMPLETED.eq(false))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public Game update(Game game) {
        dsl().update(GAME)
                .set(GAME.PLAYER_IDS, game.getPlayerIds().toArray(new Long[game.getPlayerIds().size()]))
                .set(GAME.GAME_DATA, JsonUtil.writeValue(game.getGameData()))
                .set(GAME.FINAL_SCORE, game.getFinalScore().toArray(new Integer[game.getFinalScore().size()]))
                .set(GAME.IS_COMPLETED, game.isCompleted())
                .where(GAME.ID.eq(game.getId()))
                .execute();
        // todo think about return value. What if there is no such game?
        return get(game.getId()).get();
    }

    @Override
    public void delete(Game game) {
        dsl().deleteFrom(GAME).where(GAME.ID.eq(game.getId()))
                .execute();
    }
}
