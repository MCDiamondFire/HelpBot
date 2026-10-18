package com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.database.impl.result.DatabaseResult;
import org.jetbrains.annotations.*;

import java.sql.*;
import java.util.*;

public class DFPlayerArgument extends AbstractSimpleValueArgument<Player> {
    
    public static @Nullable Player fetchPlayer(String argument) {
        DatabaseResult result = new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, argument);
                    statement.setString(2, argument);
                }))
                .compile()
                .get();
        
        if (result.isEmpty()) {
            return null;
        }
        
        try {
            ResultSet set = result.getResult();
            
            UUID uuid = UUID.fromString(set.getString("uuid"));
            String playerName = set.getString("name");
            
            return new Player(playerName, uuid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<String> getCompletions(@NotNull String input) {
        String name = input.trim();
        if (name.isEmpty()) {
            return List.of();
        }
        
        String pattern = name.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        DatabaseResult result = new DatabaseQuery()
                .query(new BasicQuery("SELECT name FROM players WHERE name LIKE ? ORDER BY name LIMIT 25;", (statement) -> {
                    statement.setString(1, pattern);
                }))
                .compile()
                .get();
        
        List<String> names = new ArrayList<>();
        if (result.isEmpty()) {
            return names;
        }
        
        try {
            for (ResultSet set : result) {
                names.add(set.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return names;
    }
    
    @Override
    public boolean hasCompletions() {
        return true;
    }
    
    @Override
    protected Player parse(@NotNull String argument, CommandEvent event) throws ArgumentException {
        Player player = fetchPlayer(argument);
        if (player == null) {
            throw new MalformedArgumentException("Player has not joined DiamondFire before, or does not exist.");
        }
        
        return player;
    }
}
