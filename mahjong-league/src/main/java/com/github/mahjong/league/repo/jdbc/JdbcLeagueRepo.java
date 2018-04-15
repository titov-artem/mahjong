package com.github.mahjong.league.repo.jdbc;

import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.common.json.JsonUtil;
import com.github.mahjong.league.model.League;
import com.github.mahjong.league.repo.LeagueRepo;
import com.github.mahjong.league.repo.TransactionalHelper;
import com.google.common.collect.Sets;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

import static com.github.mahjong.league.repo.jdbc.generated.tables.League.LEAGUE;
import static com.github.mahjong.league.repo.jdbc.generated.tables.LeaguePlayer.LEAGUE_PLAYER;

@Repository
public class JdbcLeagueRepo extends BaseRepo implements LeagueRepo {

    private static final RecordMapper<Record, League> RECORD_MAPPER = record -> {
        try {
            return new League(
                    LEAGUE.ID.get(record),
                    toTranslationsMap(JsonUtil.readValue(LEAGUE.NAME.get(record), Map.class)),
                    toTranslationsMap(JsonUtil.readValue(LEAGUE.DESCRIPTION.get(record), Map.class)),
                    Sets.newHashSet(LEAGUE.ADMINS.get(record))
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse json data", e);
        }
    };

    private final TransactionalHelper txHelper;

    private static Map<LangIso639, String> toTranslationsMap(Map source) {
        Map<LangIso639, String> out = new HashMap<>();
        for (Object entry : source.entrySet()) {
            if (!(entry instanceof Map.Entry)) {
                continue;
            }
            Map.Entry e = (Map.Entry) entry;
            LangIso639 lang = LangIso639.valueOf(e.getKey().toString());
            String translation = e.getValue().toString();
            out.put(lang, translation);
        }
        return out;
    }

    @Inject
    public JdbcLeagueRepo(DataSource dataSource,
                          TransactionalHelper txHelper) {
        super(dataSource);
        this.txHelper = txHelper;
    }

    @Override
    public List<League> getAll() {
        return dsl().select().from(LEAGUE)
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<League> getAll(Collection<Long> ids) {
        return dsl().select().from(LEAGUE).where(LEAGUE.ID.in(ids))
                .fetch()
                .map(RECORD_MAPPER);

    }

    @Override
    public List<League> getAllWithPlayer(Long playerId) {
        return dsl().select().from(LEAGUE)
                .where(LEAGUE.ID.in(
                        dsl().select(LEAGUE_PLAYER.LEAGUE_ID)
                                .from(LEAGUE_PLAYER)
                                .where(LEAGUE_PLAYER.PLAYER_ID.eq(playerId)))
                )
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public List<League> getAllWithAdmin(Long adminId) {
        return dsl().select().from(LEAGUE).where(LEAGUE.ADMINS.contains(new Long[]{adminId}))
                .fetch()
                .map(RECORD_MAPPER);
    }

    @Override
    public Optional<League> get(Long id) {
        return dsl().select().from(LEAGUE).where(LEAGUE.ID.eq(id))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public League create(League league) {
        return dsl().insertInto(LEAGUE)
                .set(LEAGUE.NAME, JsonUtil.writeValue(league.getName()))
                .set(LEAGUE.DESCRIPTION, JsonUtil.writeValue(league.getDescription()))
                .set(LEAGUE.ADMINS, league.getAdmins().toArray(new Long[league.getAdmins().size()]))
                .returning()
                .fetchOne()
                .map(RECORD_MAPPER);
    }

    @Override
    public void addAdmins(Long leagueId, Set<Long> admins) {
        dsl().update(LEAGUE)
                .set(LEAGUE.ADMINS, PostgresDSL.arrayCat(LEAGUE.ADMINS, admins.toArray(new Long[admins.size()])))
                .where(LEAGUE.ID.eq(leagueId))
                .execute();
    }

    @Override
    public void removeAdmins(Long leagueId, Set<Long> admins) {
        txHelper.defaultTx(() -> {
            Optional<League> leagueOpt = dsl().select().from(LEAGUE)
                    .where(LEAGUE.ID.eq(leagueId))
                    .forUpdate()
                    .fetchOptional(RECORD_MAPPER);
            if (!leagueOpt.isPresent()) {
                throw new IllegalArgumentException("No league " + leagueId);
            }
            Set<Long> curAdmins = new HashSet<>(leagueOpt.get().getAdmins());
            curAdmins.removeAll(admins);
            dsl().update(LEAGUE)
                    .set(LEAGUE.ADMINS, curAdmins.toArray(new Long[curAdmins.size()]))
                    .where(LEAGUE.ID.eq(leagueId))
                    .execute();
        });
    }
}
