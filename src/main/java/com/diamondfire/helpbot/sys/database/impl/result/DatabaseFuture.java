package com.diamondfire.helpbot.sys.database.impl.result;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

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
            try {
                result.getResult().close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public CompletableFuture<DatabaseResult> getFuture() {
        return CompletableFuture.completedFuture(result);
    }
    
    public DatabaseResult get() {
        return result;
    }
    
    public interface DatabaseFutureRunnable {
        
        void run(DatabaseResult resultSets) throws SQLException;
        
    }
}
