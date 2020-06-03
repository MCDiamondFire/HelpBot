package com.diamondfire.helpbot.util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionGiver {

    private static final MysqlDataSource source = new MysqlDataSource();

    static {
        source.setUrl(SensitiveData.DB_URL);
        source.setUser(SensitiveData.DB_USER);
        source.setPassword(SensitiveData.DB_PASS);
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

}
