package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;

import java.util.*;

public class CpGraphCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "cpgraph";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph showing a players previous cp.")
                .category(CommandCategory.STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player")
                );
    }


    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder();
        new SingleQueryBuilder()

                .query("SELECT * FROM players WHERE uuid = ? OR name = ?;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery(table -> {
                    String name = table.getString("name");
                    String uuid = table.getString("uuid");
                    new SingleQueryBuilder()
                            .query("SELECT DATE_FORMAT(date, '%d-%m') AS time,points FROM owen.creator_rankings_log WHERE uuid = ?;", (statement) -> statement.setString(1, uuid))
                            .onQuery((resultTable) -> {
                                Map<GraphableEntry<?>, Integer> entries = new LinkedHashMap<>();
                                do {
                                    entries.put(new StringEntry(resultTable.getString("time")), resultTable.getInt("points"));
                                } while (resultTable.next());

                                event.getChannel().sendFile(new ChartGraphBuilder()
                                        .setGraphName(name + "'s CP Graph")
                                        .createGraph(entries)).queue();
                            })
                            .onNotFound(() -> {
                                preset.withPreset(
                                        new InformativeReply(InformativeReplyType.ERROR, "No data was found on this player. Note, their cp must be above 4990 to start tracking.")
                                );
                            }).execute();
                })
                .onNotFound(() -> {
                    preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                }).execute();

        event.reply(preset);
    }

}