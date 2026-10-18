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
import net.dv8tion.jda.api.utils.*;

import java.sql.ResultSet;
import java.util.*;

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
                .query(new BasicQuery("SELECT owner_name, multiplier, end_time FROM xp_boosters WHERE end_time > UNIX_TIMESTAMP() * 1000 ORDER BY end_time"))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        embed.setDescription("There are no active boosters right now. How sad... :(");
                        event.reply(preset);
                        return;
                    }
                    
                    List<String> lines = new ArrayList<>();
                    for (ResultSet set : result) {
                        String owner = MarkdownSanitizer.escape(set.getString("owner_name"));
                        int multiplier = set.getInt("multiplier");
                        long endTime = set.getLong("end_time");
                        
                        lines.add(String.format("%s**%sx** from **%s** \u2022 ends %s",
                                getEmote(event, multiplier), multiplier, owner, TimeFormat.RELATIVE.format(endTime)));
                    }
                    
                    embed.setDescription(String.join("\n", lines));
                    event.reply(preset);
                });
        
    }
    
    private static String getEmote(CommandEvent event, int multiplier) {
        Emoji emote = event.getJDA().getEmojiById(emotes[Util.clamp(multiplier - 2, 0, emotes.length - 1)]);
        return emote == null ? "" : emote.getFormatted() + " ";
    }
    
}


