package com.github.mahjong.league.repo.jdbc;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

public abstract class BaseRepo {

    private final DSLContext dsl;

    protected BaseRepo(DataSource dataSource) {
        this.dsl = DSL.using(dataSource, SQLDialect.POSTGRES_9_5);
    }

    protected final DSLContext dsl() {
        return dsl;
    }
}
