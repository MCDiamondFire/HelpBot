package com.diamondfire.helpbot.sys.database.impl.result;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

/**
 * A query's result and the connection it holds. {@link #run} always returns the connection to the pool,
 * so results are only read through it.
 */
public class DatabaseFuture {

    private final DatabaseResult result;
    private final Connection connection;

    public DatabaseFuture(Connection connection, DatabaseResult result) {
        this.connection = connection;
        this.result = result;
    }

    public void runAsync(DatabaseFutureRunnable action) {
        CompletableFuture.runAsync(() -> {
            run(action);
        });
    }

    public void run(DatabaseFutureRunnable action) {
        try {
            action.run(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            ResultSet set = result.getRawSet();
            if (set != null) {
                set.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public interface DatabaseFutureRunnable {

        void run(DatabaseResult resultSets) throws SQLException;

    }
}
