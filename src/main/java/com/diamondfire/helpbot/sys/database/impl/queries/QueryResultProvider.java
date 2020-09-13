package com.diamondfire.helpbot.sys.database.impl.queries;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

public interface QueryResultProvider {

    ResultSet execute(@NotNull Connection connection) throws SQLException;

}
