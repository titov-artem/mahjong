package com.github.mahjong.main.repo.jdbc;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.repo.PlayerRepo;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.github.mahjong.main.repo.jdbc.generated.tables.Player.PLAYER;

@Repository
public class JdbcPlayerRepo extends BaseRepo implements PlayerRepo {

    private static final RecordMapper<Record, Player> RECORD_MAPPER = record -> new Player(
            PLAYER.ID.get(record),
            PLAYER.LOGIN.get(record),
            PLAYER.NAME.get(record),
            LangIso639.valueOf(PLAYER.LANG.get(record))
    );

    @Inject
    public JdbcPlayerRepo(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Player create(Player player) {
        return dsl().insertInto(PLAYER)
                .set(PLAYER.LOGIN, player.getLogin())
                .set(PLAYER.NAME, player.getName())
                .set(PLAYER.LANG, player.getLang().name())
                .returning()
                .fetchOne()
                .map(RECORD_MAPPER);
    }

    @Override
    public Optional<Player> get(Long id) {
        return dsl().select().from(PLAYER).where(PLAYER.ID.eq(id))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public Optional<Player> getByLogin(String login) {
        return dsl().select().from(PLAYER).where(PLAYER.LOGIN.eq(login))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public List<Player> getAll() {
        return dsl().select().from(PLAYER)
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<Player> getAll(Collection<Long> ids) {
        return dsl().select().from(PLAYER).where(PLAYER.ID.in(ids))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public Optional<Player> update(Player player) {
        return dsl().update(PLAYER)
                .set(PLAYER.LOGIN, player.getLogin())
                .set(PLAYER.NAME, player.getName())
                .set(PLAYER.LANG, player.getLang().name())
                .where(PLAYER.ID.eq(player.getId()))
                .returning()
                .fetchOptional()
                .map(RECORD_MAPPER::map);
    }

    @Override
    public void delete(Player player) {
        dsl().delete(PLAYER).where(PLAYER.ID.eq(player.getId()));
    }
}
