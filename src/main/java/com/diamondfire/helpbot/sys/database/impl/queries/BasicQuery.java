package com.diamondfire.helpbot.sys.database.impl.queries;

import com.mysql.jdbc.StatementImpl;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.*;

import java.sql.*;

public class BasicQuery implements QueryResultProvider {

    private final String query;
    private final PreparedStatementProvider provider;

    public BasicQuery(@NotNull @Language("SQL") String query) {
        this.query = query;
        this.provider = null;
    }

    public BasicQuery(@NotNull @Language("SQL") String query, @Nullable PreparedStatementProvider provider) {
        this.query = query;
        this.provider = provider;
    }

    @Override
    public ResultSet execute(@NotNull Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setQueryTimeout(5);

        if (provider != null) {
            provider.prepare(statement);
        }

        if (statement.execute()) {
            ResultSet set = statement.getResultSet();
            try {
                // Close prepared statement but not the resultsets. Very weird I have to do this...
                StatementImpl.class.getDeclaredMethod("realClose", boolean.class, boolean.class).invoke(statement, true, false);
            } catch (Exception ignored) {
            }

            return set;
        } else {
            statement.close();
            connection.close();
            return null;
        }
    }

    public interface PreparedStatementProvider {

        void prepare(@NotNull PreparedStatement statement) throws SQLException;

    }
}