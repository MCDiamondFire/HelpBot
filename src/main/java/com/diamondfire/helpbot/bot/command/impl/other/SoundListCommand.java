package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;

public class SoundListCommand extends AbstractFileListCommand {
    
    @Override
    public String getName() {
        return "sounds";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"soundlist"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a file that contains all sounds.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        super.generate(event, CodeDatabase.getRegistry(CodeDatabase.SOUNDS));
    }
    
}
