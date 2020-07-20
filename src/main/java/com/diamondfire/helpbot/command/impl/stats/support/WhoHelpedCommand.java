package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.command.reply.feature.informative.*;
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
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new MinecraftUserPreset(player),
                        new InformativeReply(InformativeReplyType.INFO, "Players who have helped " + player, null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT COUNT(staff) AS total, staff FROM support_sessions WHERE name = ? GROUP BY staff ORDER BY count(staff) DESC;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((query) -> {
                    List<String> sessions = new ArrayList<>();

                    do {
                        sessions.add(query.getInt("total") + " " + query.getString("staff"));
                    } while (query.next());

                    Util.addFields(embed, sessions, true);

                })
                .onNotFound(() -> {
                    embed.setDescription("Nobody!");
                }).execute();
        event.reply(preset);
    }

}
