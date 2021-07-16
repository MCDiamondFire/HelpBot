package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.ResultSet;
import java.time.Instant;

public class QueueCommand extends Command {
    
    @Override
    public String getName() {
        return "queue";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"que", "q"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current queue.")
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
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder embed = preset.getEmbed();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT player, plot, node, staff, TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP(), enter_time)) AS time FROM support_queue ORDER BY enter_time LIMIT 25"))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        embed.setTitle("Queue is Empty!");
                        embed.setDescription("Keep up the good work");
                        embed.setColor(new Color(0, 234, 23));
                        return;
                    }
                    
                    int i = 0;
                    for (ResultSet set : result) {
                        embed.addField(StringUtil.display(set.getString("player")), FormatUtil.formatMilliTime(Instant.EPOCH.plusSeconds(set.getInt("time")).toEpochMilli()), false);
                        i++;
                    }
                    embed.setTitle(String.format("Players in Queue (%s)", i));
                    embed.setColor(colorAmt(i));
                });
        
        event.reply(preset);
        
    }
    
    private Color colorAmt(int index) {
        switch (index) {
            case 1:
                return new Color(0, 234, 23);
            case 2:
                return new Color(38, 127, 0);
            case 3:
                return new Color(255, 216, 0);
            case 4:
                return new Color(255, 100, 0);
            case 5:
                return new Color(255, 0, 0);
            default:
                return new Color(127, 0, 0);
        }
    }
    
}

