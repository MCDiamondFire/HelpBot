package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class JoinsCommand extends Command {
    
    @Override
    public String getName() { return "joins"; }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Shows the total amount of unique players that have joined DiamondFire before.")
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() { return new ArgumentSet(); }
    
    @Override
    public Permission getPermission() { return Permission.USER; }
    
    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        
        builder.setTitle("Total Joins");
        
        // gets the total amount of players that have joined before
        new DatabaseQuery()
                .query(new BasicQuery("SELECT players.name, COUNT(*) as count"))
                .compile()
                .run((result) -> {
                    String count;
                    if(result.isEmpty()) {
                        count = "None (:rotating_light: this should not be happening :rotating_light:)";
                    } else {
                        count = FormatUtil.formatNumber(result.getResult().getInt("count"));
                    }
                    
                    builder.setDescription(String.format("A total of %s players have joined DiamondFire before!", count));
                });
        
        event.getChannel().sendMessage(builder.build()).queue();
    }
}