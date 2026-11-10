package com.diamondfire.helpbot.sys.database;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.config.Config;
import com.zaxxer.hikari.*;

import java.sql.*;

public class ConnectionProvider {

    private static final HikariDataSource source;

    static {
        Config config = HelpBotInstance.getConfig();

        HikariConfig hikari = new HikariConfig();
        // The config may still use the old MySQL driver's scheme; the MariaDB driver needs its own.
        hikari.setJdbcUrl(config.getDBUrl().replaceFirst("^jdbc:mysql:", "jdbc:mariadb:"));
        hikari.setUsername(config.getDBUser());
        hikari.setPassword(config.getDBPassword());
        hikari.setPoolName("helpbot-db");
        hikari.setMaximumPoolSize(10);
        hikari.setMinimumIdle(2);
        hikari.setConnectionTimeout(10_000);
        hikari.setIdleTimeout(300_000);
        hikari.setMaxLifetime(1_800_000);
        hikari.setKeepaliveTime(120_000);
        // Logs a stack trace for any connection held longer than this, so leaks show up.
        hikari.setLeakDetectionThreshold(30_000);

        source = new HikariDataSource(hikari);
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

}
