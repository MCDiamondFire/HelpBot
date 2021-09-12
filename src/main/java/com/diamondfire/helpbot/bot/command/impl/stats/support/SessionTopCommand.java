package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;

public class SessionTopCommand extends Command {
    
    @Override
    public String getName() {
        return "top";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"sessiontop"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets support members with the top sessions in a certain number of days.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("days OR all")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("days",
                        new AlternateArgumentContainer<>(
                                new SingleArgumentContainer<>(new ClampedIntegerArgument(1)),
                                new SingleArgumentContainer<>(new DefinedObjectArgument<>("all"))
                        ).optional(event -> 30)
                );
    }
    
    @Override
    public Rank getRank() {
        return Rank.SUPPORT;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder();
        Object arg = event.getArgument("days");
        
        if (arg instanceof String) {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.INFO, "Top Support Members of all time", null)
            );
            EmbedBuilder embed = preset.getEmbed();
            
            new DatabaseQuery()
                    .query(new BasicQuery("SELECT DISTINCT staff, COUNT(*) as sessions FROM support_sessions GROUP BY staff ORDER BY sessions DESC LIMIT 10"))
                    .compile()
                    .run((result) -> {
                        for (ResultSet set : result) {
                            embed.addField(StringUtil.display(set.getString("staff")),
                                    "\nSessions: " + FormatUtil.formatNumber(set.getInt("sessions")), false);
                        }
                        
                    });
        } else {
            int days = (int) arg;
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.INFO, String.format("Top Support Members in %s %s", days, StringUtil.sCheck("day", days)), null)
            );
            EmbedBuilder embed = preset.getEmbed();
            
            new DatabaseQuery()
                    .query(new BasicQuery("SELECT DISTINCT staff, COUNT(*) as sessions FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY GROUP BY staff ORDER BY sessions DESC LIMIT 10", (statement) -> statement.setInt(1, days)))
                    .compile()
                    .run((result) -> {
                        for (ResultSet set : result) {
                            embed.addField(StringUtil.display(set.getString("staff")),
                                    "\nSessions: " + FormatUtil.formatNumber(set.getInt("sessions")), false);
                        }
                        
                    });
        }
        
        event.reply(preset);
    }
    
}


