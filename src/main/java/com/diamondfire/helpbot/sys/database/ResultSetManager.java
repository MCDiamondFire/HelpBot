package com.diamondfire.helpbot.sys.database;

import java.sql.*;

@FunctionalInterface
public interface ResultSetManager {

    void run(ResultSet set) throws SQLException;
}
