package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.bot.restart.RestartHandler;
import net.dv8tion.jda.api.EmbedBuilder;

public class RestartCommand extends Command {
    
    @Override
    public String getName() {
        return "restart";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"stop", "exit"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Restarts the bot.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Restarting!");
        builder.setDescription("This may take a moment");
        
        event.getChannel().sendMessage(builder.build()).queue((msg) -> {
            RestartHandler.logRestart(msg);
            System.exit(0);
        });
        
    }
    
}


