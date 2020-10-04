package com.diamondfire.helpbot.df.creator.requirements;

import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.database.impl.result.DatabaseResult;

import java.sql.SQLException;

public class DynamicRequirementProvider implements RequirementProvider {

    private final String identifier;

    public DynamicRequirementProvider(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getRequirement() {
        DatabaseResult result = new DatabaseQuery()
                .query(new BasicQuery("SELECT req FROM hypercube.creator_req WHERE tier = ?", (statement) -> statement.setString(1, identifier)))
                .compile().get();

        try {
            return result.getResult().getInt("req");
        } catch (SQLException ignored) {
            return 0;
        }
    }
}
