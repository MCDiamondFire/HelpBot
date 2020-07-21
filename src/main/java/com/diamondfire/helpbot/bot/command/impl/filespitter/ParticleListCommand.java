package com.diamondfire.helpbot.bot.command.impl.filespitter;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class ParticleListCommand extends AbstractFileListCommand {

    @Override
    public String getName() {
        return "particles";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"particlelist"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a file that contains all particles.")
                .category(CommandCategory.OTHER);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        super.generate(event, CodeDatabase.getParticles());
    }

}
