package com.github.mahjong.security.repo.jdbc;

import com.github.mahjong.security.model.AuthToken;
import com.github.mahjong.security.repo.AuthTokenRepo;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Optional;

import static com.github.mahjong.security.repo.jdbc.generated.tables.AuthToken.AUTH_TOKEN;

@Repository
public class JdbcAuthTokenRepo implements AuthTokenRepo {

    private static final RecordMapper<Record, AuthToken> RECORD_MAPPER = record ->
            new AuthToken(
                    AUTH_TOKEN.LOGIN.get(record),
                    AUTH_TOKEN.TOKEN.get(record),
                    AUTH_TOKEN.EXPIRE_AT.get(record)
            );

    private final DSLContext dsl;

    @Inject
    public JdbcAuthTokenRepo(DataSource dataSource) {
        this.dsl = DSL.using(dataSource, SQLDialect.POSTGRES_9_5);
    }


    @Override
    public AuthToken create(AuthToken token) {
        return dsl.insertInto(AUTH_TOKEN)
                .set(AUTH_TOKEN.LOGIN, token.getLogin())
                .set(AUTH_TOKEN.TOKEN, token.getToken())
                .set(AUTH_TOKEN.EXPIRE_AT, token.getExpireAt())
                .returning()
                .fetchOne()
                .map(RECORD_MAPPER);
    }

    @Override
    public Optional<AuthToken> get(String login, String token) {
        return dsl.select().from(AUTH_TOKEN).where(AUTH_TOKEN.LOGIN.eq(login))
                .and(AUTH_TOKEN.TOKEN.eq(token))
                .fetchOptional(RECORD_MAPPER);
    }

    @Override
    public Optional<AuthToken> getNewest(String login) {
        return dsl.select().from(AUTH_TOKEN).where(AUTH_TOKEN.LOGIN.eq(login))
                .and(AUTH_TOKEN.EXPIRE_AT.eq(
                        dsl.select(DSL.max(AUTH_TOKEN.EXPIRE_AT)).from(AUTH_TOKEN).where(AUTH_TOKEN.LOGIN.eq(login))
                ))
                .limit(1)
                .fetchOptional(RECORD_MAPPER);
    }
}
