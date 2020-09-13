package com.diamondfire.helpbot.sys.database.impl;

import com.diamondfire.helpbot.sys.database.ConnectionProvider;
import com.diamondfire.helpbot.sys.database.impl.queries.QueryResultProvider;
import com.diamondfire.helpbot.sys.database.impl.result.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class DatabaseQuery {

    private QueryResultProvider queryProvider = null;

    public DatabaseQuery query(@NotNull QueryResultProvider queryProvider) {
        this.queryProvider = queryProvider;
        return this;
    }

    public DatabaseFuture compile() {
        try {
            return new DatabaseFuture(new DatabaseResult(queryProvider.execute(ConnectionProvider.getConnection())));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute SQL !" + e.getMessage());
        }
    }
}
