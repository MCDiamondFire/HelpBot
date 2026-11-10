package com.diamondfire.helpbot.df.creator.requirements;

import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;

import java.sql.ResultSet;

public class DynamicRequirementProvider implements RequirementProvider {
    
    private final String identifier;
    
    public DynamicRequirementProvider(String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public int getRequirement() {
        int[] requirement = new int[1];
        new DatabaseQuery()
                .query(new BasicQuery("SELECT req FROM creator_req WHERE tier = ?", (statement) -> statement.setString(1, identifier)))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        requirement[0] = set.getInt("req");
                    }
                });
        
        return requirement[0];
    }
}
