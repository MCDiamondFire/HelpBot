package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReply;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReplyType;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

public class LastJoinedCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "lastjoined";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"lastseen", "seen", "lastonline", "lastjoin"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the last date when a user joined.")
                .category(CommandCategory.PLAYER_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new MinecraftUserPreset(player.name()),
                        new InformativeReply(InformativeReplyType.INFO, null, null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT time AS time FROM player_join_log WHERE uuid = ? ORDER BY time DESC LIMIT 1;", (statement) -> statement.setString(1, player.uuidString())))
                .compile()
                .run((resultTableDate) -> {
                    if (resultTableDate.isEmpty()) {
                        embed.addField("Last Seen", "A long time ago...", false);
                        return;
                    }
                    
                    ResultSet setTime = resultTableDate.getResult();
                    Timestamp date = setTime.getTimestamp("time");
                    embed.addField("Last Seen", TimeFormat.RELATIVE.format(date.getTime()), false);
    
                });
        
        event.reply(preset);
    }
}
