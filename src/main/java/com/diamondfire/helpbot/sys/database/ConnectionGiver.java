package com.diamondfire.helpbot.sys.database;

import com.diamondfire.helpbot.bot.config.Config;
import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.*;

public class ConnectionGiver {

    private static final MysqlDataSource source = new MysqlDataSource();

    static {
        Config config = HelpBotInstance.getConfig();
        source.setUrl(config.getDBUrl());
        source.setUser(config.getDBUser());
        source.setPassword(config.getDBPassword());
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

}
