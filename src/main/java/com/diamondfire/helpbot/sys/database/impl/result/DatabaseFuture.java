package com.diamondfire.helpbot.sys.database.impl.result;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DatabaseFuture {

    private final DatabaseResult result;

    public DatabaseFuture(DatabaseResult result) {
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
        } finally {
            try {
                result.getResult().close();
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
