package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class HelpedByCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "helpedby";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the first 100 players that were helped by a certain staff member.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("player")
                                .optional()
                );
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    protected void execute(CommandEvent event, String player) {

        new SingleQueryBuilder()
                .query("SELECT COUNT(name) AS total, name FROM support_sessions WHERE staff = ? GROUP BY name ORDER BY count(name) DESC LIMIT 25;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((query) -> {
                    List<String> sessions = new ArrayList<>();
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle(String.format("Players %s has Helped", player));
                    builder.setAuthor(player, null, "https://mc-heads.net/head/" + player);

                    do {
                        sessions.add(query.getInt("total") + " " + query.getString("name"));
                    } while (query.next());

                    Util.addFields(builder, sessions, true);
                    event.getChannel().sendMessage(builder.build()).queue();

                })
                .onNotFound(() -> {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Hmm.. It seems like " + player + " hasn't helped anybody.");
                    event.getChannel().sendMessage(builder.build()).queue();
                }).execute();

    }

}
