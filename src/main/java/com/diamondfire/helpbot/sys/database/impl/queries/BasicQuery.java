package com.diamondfire.helpbot.sys.database.impl.queries;

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
        // Scrollable, as DatabaseResult rewinds the result set to check for rows.
        // The statement stays open with its result set; both close when the connection is closed.
        PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        statement.setQueryTimeout(10);

        if (provider != null) {
            provider.prepare(statement);
        }

        if (statement.execute()) {
            return statement.getResultSet();
        }

        statement.close();
        connection.close();
        return null;
    }

    public interface PreparedStatementProvider {

        void prepare(@NotNull PreparedStatement statement) throws SQLException;

    }
}
