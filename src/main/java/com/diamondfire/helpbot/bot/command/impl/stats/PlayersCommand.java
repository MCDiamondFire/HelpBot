package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayersCommand extends Command {
    
    @Override
    public String getName() {
        return "players";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"currentplayers", "playerc", "nodes", "playerlist", "nodelist", "nodestatus", "count"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current player count on each node.")
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Current Players:", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        AtomicInteger totalPlayers = new AtomicInteger();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM node_info"))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        int nodeCount = set.getInt("player_count");
                        embed.addField("Node " + set.getInt("node"), "Players: " + nodeCount, false);
                        totalPlayers.addAndGet(nodeCount);
                    }
                });
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM hypercube_beta.node_info"))
                .compile()
                .run((resultBeta) -> {
                    for (ResultSet set : resultBeta) {
                        int nodeCount = set.getInt("player_count");
                        embed.addField("Node Beta", "Players: " + nodeCount, false);
                        totalPlayers.addAndGet(nodeCount);
                    }
                    embed.addField("All Nodes", "Players: " + totalPlayers, false);
                    
                    if (totalPlayers.get() == 0) {
                        embed.addField(":rotating_light: DF BROKE :rotating_light:", "Jere, fix it already! >:(", false);
                    }
                });
        event.reply(preset);
    }
    
}


