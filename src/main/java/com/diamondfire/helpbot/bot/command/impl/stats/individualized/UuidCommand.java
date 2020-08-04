package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.ranks.Ranks;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


//Command exists for easy mobile copy and pasting
public class UuidCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {

        new SingleQueryBuilder()
                .query("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery(table -> {
                    String playerUUID = table.getString("uuid");

                    event.getChannel().sendMessage(playerUUID);
                })
                .onNotFound(() -> {
                    PresetBuilder preset = new PresetBuilder();
                    preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                    event.reply(preset);
                }).execute();
    }

}


