package com.diamondfire.helpbot.components.database;
import com.diamondfire.helpbot.components.config.Config;
import com.diamondfire.helpbot.instance.BotInstance;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionGiver {

    private static final MysqlDataSource source = new MysqlDataSource();

    static {
        Config config = BotInstance.getConfig();
        source.setUrl(config.getDBUrl());
        source.setUser(config.getDBUser());
        source.setPassword(config.getDBPassword());
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

}
