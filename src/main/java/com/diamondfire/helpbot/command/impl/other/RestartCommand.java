package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.CommandCategory;
import com.diamondfire.helpbot.command.help.HelpContext;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.restart.RestartHandler;
import com.diamondfire.helpbot.events.CommandEvent;
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
                .description("Restarts bot")
                .category(CommandCategory.OTHER);
    }

    @Override
    public ArgumentSet getArguments() {
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


