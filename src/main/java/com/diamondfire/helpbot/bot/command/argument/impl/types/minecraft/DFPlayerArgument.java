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
        Player[] player = new Player[1];
        new DatabaseQuery()
                .query(new BasicQuery("SELECT uuid, name FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1", (statement) -> {
                    statement.setString(1, argument);
                    statement.setString(2, argument);
                }))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        player[0] = new Player(set.getString("name"), UUID.fromString(set.getString("uuid")));
                    }
                });
        
        return player[0];
    }
    
    /**
     * @return the player linked to this Discord account, or null if not linked
     */
    public static @Nullable Player fetchLinkedPlayer(long discordId) {
        Player[] player = new Player[1];
        new DatabaseQuery()
                .query(new BasicQuery("SELECT players.uuid, players.name FROM linked_accounts, players " +
                        "WHERE linked_accounts.player_uuid = players.uuid AND linked_accounts.discord_id = ? LIMIT 1",
                        (statement) -> statement.setLong(1, discordId)))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        player[0] = new Player(set.getString("name"), UUID.fromString(set.getString("uuid")));
                    }
                });
        
        return player[0];
    }
    
    @Override
    public List<String> getCompletions(@NotNull String input) {
        String name = input.trim();
        if (name.isEmpty()) {
            return List.of();
        }
        
        String pattern = name.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        List<String> names = new ArrayList<>();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT name FROM players WHERE name LIKE ? ORDER BY name LIMIT 25", (statement) -> {
                    statement.setString(1, pattern);
                }))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        names.add(set.getString("name"));
                    }
                });
        
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
