package com.diamondfire.helpbot.sys.database;

import java.sql.*;

@FunctionalInterface
public interface PreparedStatementManager {

    void run(PreparedStatement statement) throws SQLException;
}
