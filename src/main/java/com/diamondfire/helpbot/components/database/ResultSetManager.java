package com.diamondfire.helpbot.components.database;

import java.sql.*;

@FunctionalInterface
public interface ResultSetManager {

    void run(ResultSet set) throws SQLException;
}
