package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class CountSamquotesSubCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "count";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the total number of stored samquotes.");
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        String[] strings = ExternalFiles.SAM_DIR.list();
    
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Total Sam Quotes:");
        builder.setDescription("" + strings.length);
        builder.setColor(new Color(87, 177, 71));
    
        event.getReplyHandler().reply(builder);
    }
}
