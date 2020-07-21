package com.diamondfire.helpbot.df.creator.requirements;

import com.diamondfire.helpbot.sys.database.ConnectionGiver;

import java.sql.*;

public class DynamicRequirementProvider implements RequirementProvider {

    private final String identifier;

    public DynamicRequirementProvider(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getRequirement() {
        //TODO returning db system

        try (Connection connection = ConnectionGiver.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT req FROM hypercube.creator_req WHERE tier = ?")) {
            statement.setString(1, identifier);

            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return set.getInt("req");
            }
            set.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
