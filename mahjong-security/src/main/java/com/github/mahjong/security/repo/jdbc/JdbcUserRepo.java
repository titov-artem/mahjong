package com.github.mahjong.security.repo.jdbc;

import com.github.mahjong.security.model.MahjongUser;
import com.github.mahjong.security.repo.UserRepo;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Optional;

import static com.github.mahjong.security.repo.jdbc.generated.tables.MahjongUser.MAHJONG_USER;

@Repository
public class JdbcUserRepo implements UserRepo {

    private static final RecordMapper<Record, MahjongUser> RECORD_MAPPER = record -> new MahjongUser(
            MAHJONG_USER.LOGIN.get(record),
            MAHJONG_USER.PASSWORD.get(record)
    );

    private final DSLContext dsl;

    @Inject
    public JdbcUserRepo(DataSource dataSource) {
        this.dsl = DSL.using(dataSource, SQLDialect.POSTGRES_9_5);
    }


    @Override
    public MahjongUser create(MahjongUser user) {
        dsl.insertInto(MAHJONG_USER)
                .set(MAHJONG_USER.LOGIN, user.getLogin())
                .set(MAHJONG_USER.PASSWORD, user.getPassword())
                .execute();
        return user;
    }

    @Override
    public Optional<MahjongUser> get(String login) {
        return dsl.select().from(MAHJONG_USER).where(MAHJONG_USER.LOGIN.eq(login))
                .fetchOptional(RECORD_MAPPER);
    }

}
