package com.diamondfire.helpbot.sys.database.impl;

import com.diamondfire.helpbot.sys.database.ConnectionProvider;
import com.diamondfire.helpbot.sys.database.impl.queries.QueryResultProvider;
import com.diamondfire.helpbot.sys.database.impl.result.*;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class DatabaseQuery {
    
    private QueryResultProvider queryProvider = null;
    
    public DatabaseQuery query(@NotNull QueryResultProvider queryProvider) {
        this.queryProvider = queryProvider;
        return this;
    }
    
    public DatabaseFuture compile() {
        Connection connection = null;
        try {
            connection = ConnectionProvider.getConnection();
            return new DatabaseFuture(connection, new DatabaseResult(queryProvider.execute(connection)));
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
            
            throw new IllegalStateException("Failed to execute SQL! " + e.getMessage(), e);
        }
    }
}
