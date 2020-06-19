package com.diamondfire.helpbot.command.impl.filespitter;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.events.CommandEvent;

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
    public String getDescription() {
        return "Generates a file that contains all current particles.";
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
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
