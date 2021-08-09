package com.diamondfire.helpbot.bot.command.impl.other.info;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class ContributeCommand extends Command {
    
    @Override
    public String getName() {
        return "contribute";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext().category(CommandCategory.OTHER).description("Gives directions on how to contribute to HelpBot");
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
    
        PresetBuilder res = new PresetBuilder()
                .withPreset(new InformativeReply(InformativeReplyType.INFO,"How to Contribute",
                        "You may contribute to HelpBot by forking it on github(https://github.com/MCDiamondFire/HelpBot)\n " +
                                "then creating a pull request"));
        event.reply(res);
        
    }
}
