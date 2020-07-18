package com.diamondfire.helpbot.components.database;

import java.sql.*;

@FunctionalInterface
public interface PreparedStatementManager {

    void run(PreparedStatement statement) throws SQLException;
}
