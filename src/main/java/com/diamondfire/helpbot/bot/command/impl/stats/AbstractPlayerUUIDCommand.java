package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.database.impl.result.DatabaseResult;
import com.diamondfire.helpbot.util.Util;

import java.sql.*;
import java.util.UUID;

public abstract class AbstractPlayerUUIDCommand extends Command {
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("player",
                        new SingleArgumentContainer<>(new DFPlayerArgument()).optional(null));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Player player = event.getArgument("player");
        if (player == null) {
            player = resolveSelf(event);
            if (player == null) {
                return;
            }
        }
        
        execute(event, player);
    }
    
    /**
     * Resolves the player that ran the command, preferring their linked account.
     *
     * @return the player, or null if an error was already replied
     */
    private static Player resolveSelf(CommandEvent event) {
        Player linked = fetchLinkedPlayer(event.getAuthor().getIdLong());
        if (linked != null) {
            return linked;
        }
        
        // Fall back to the member's name. Non-verified members can set their display name
        // to any player and run commands as that player; therefore, they must be verified.
        if (!event.getMember().getRoles().contains(event.getGuild().getRoleById(Util.VERIFIED))) {
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "You must be verified to use this command!")
                    ));
            return null;
        }
        
        Player player = DFPlayerArgument.fetchPlayer(event.getMember().getEffectiveName());
        if (player == null) {
            event.reply(new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Could not find your Minecraft account. Please specify a player.")
                    ));
        }
        
        return player;
    }
    
    private static Player fetchLinkedPlayer(long discordId) {
        DatabaseResult result = new DatabaseQuery()
                .query(new BasicQuery("SELECT players.uuid, players.name FROM linked_accounts, players " +
                        "WHERE linked_accounts.player_uuid = players.uuid AND linked_accounts.discord_id = ? LIMIT 1",
                        (statement) -> statement.setLong(1, discordId)))
                .compile()
                .get();
        
        if (result.isEmpty()) {
            return null;
        }
        
        try {
            ResultSet set = result.getResult();
            return new Player(set.getString("name"), UUID.fromString(set.getString("uuid")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected abstract void execute(CommandEvent event, Player player);
    
}


