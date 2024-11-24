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
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.sql.ResultSet;

public class BoostersCommand extends Command {
    private static final long[] emotes = new long[]{809172442957217812L, 809172442496630815L};
    
    @Override
    public String getName() {
        return "boosters";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"booster", "cpbooster", "boost", "boosts", "currentboosts", "cpboosts"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current active boosters on all nodes.")
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
                        new InformativeReply(InformativeReplyType.INFO, "Current Boosters", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM xp_boosters WHERE FROM_UNIXTIME(end_time * 0.001) > CURRENT_TIMESTAMP()"))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        embed.addField("None!", "How sad... :(", false);
                    }
                    
                    for (ResultSet set : result) {
                        String owner = set.getString("owner_name");
                        int multiplier = set.getInt("multiplier");
                        String durationName = FormatUtil.formatMilliTime(set.getLong("end_time") - System.currentTimeMillis());
                        
                        Emoji emote = event.getJDA().getEmojiById(emotes[Util.clamp(multiplier - 2, 0, emotes.length - 1)]);
                        embed.addField(String.format("%s %sx booster from %s ", emote.getFormatted(), multiplier, owner), String.format("Ends in: %s", durationName), false);
                    }
                    
                    event.reply(preset);
                });
        
    }
    
}


