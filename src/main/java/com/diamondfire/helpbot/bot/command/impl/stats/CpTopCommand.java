package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.ClampedIntegerArgument;
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

import java.sql.ResultSet;

public class CpTopCommand extends Command {
    
    @Override
    public String getName() {
        return "cptop";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"cpleaderboard"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current CP leaderboard.")
                .category(CommandCategory.GENERAL_STATS)
                .addArgument(new HelpContextArgument().name("leaderboard place").optional());
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument("place",
                new SingleArgumentContainer<>(new ClampedIntegerArgument(1)).optional(1)
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        int startingPlace = event.getArgument("place");
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "CP Leaderboard", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM creator_rankings ORDER BY points DESC LIMIT 10 OFFSET ?", (statement) -> statement.setInt(1, startingPlace - 1)))
                .compile()
                .run((resultTable) -> {
                    int place = startingPlace;
                    for (ResultSet set : resultTable) {
                        embed.addField(String.format("%s. ", FormatUtil.formatNumber(place)) + StringUtil.display(set.getString("name")),
                                "CP: " + FormatUtil.formatNumber(set.getInt("points")), false);
                        place++;
                    }
                    event.reply(preset);
                });
        
    }
    
}


