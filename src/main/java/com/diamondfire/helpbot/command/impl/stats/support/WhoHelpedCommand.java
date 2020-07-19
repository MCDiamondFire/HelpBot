package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class WhoHelpedCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "whohelped";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets all staff members that helped a certain player. Name changes are not counted, so use ?names to find previous names.")
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
                .query("SELECT COUNT(staff) AS total, staff FROM support_sessions WHERE name = ? GROUP BY staff ORDER BY count(staff) DESC;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((query) -> {
                    List<String> sessions = new ArrayList<>();
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Players who have helped " + player);
                    builder.setAuthor(player, null, "https://mc-heads.net/head/" + player);

                    do {
                        sessions.add(query.getInt("total") + " " + query.getString("staff"));
                    } while (query.next());

                    Util.addFields(builder, sessions, true);
                    event.getChannel().sendMessage(builder.build()).queue();

                })
                .onNotFound(() -> {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Hmm.. It seems like nobody has helped " + player);
                    event.getChannel().sendMessage(builder.build()).queue();
                }).execute();

    }

}
