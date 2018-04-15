package com.github.mahjong.main.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Value("${datasource.driverClassName:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${datasource.url:jdbc:postgresql://localhost:5432/mahjong_main}")
    private String jdbcUrl;

    @Value("${datasource.user:mahjong_main}")
    private String user;

    @Value("${datasource.password:mahjong_main}")
    private String password;

    @Value("${datasource.isolationLevel:2}")
    private String isolationLevel;

    @Value("${datasource.pool.validationQuery:select 1}")
    private String validationQuery;

    @Value("${datasource.pool.autoCommit:true}")
    private boolean autoCommit;

    @Value("${datasource.pool.readOnly:false}")
    private boolean readOnly;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);

        config.setTransactionIsolation(isolationLevel);
        config.setConnectionTestQuery(validationQuery);
        config.setAutoCommit(autoCommit);
        config.setReadOnly(readOnly);

        config.setPoolName("jdbc.hikaricp.mahjong-main");

        return new HikariDataSource(config);
    }

    @DependsOn(value = "dataSource")
    @Bean(name = "flyway")
    public Flyway flywayProduction(DataSource dataSource) {
        Flyway flyway = configureFlyway(dataSource, "db/init", "db/migration");
        flyway.migrate();
        return flyway;
    }

    private static Flyway configureFlyway(DataSource dataSource, String... locations) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations(locations);
        flyway.setBaselineOnMigrate(true);
        return flyway;
    }

}
