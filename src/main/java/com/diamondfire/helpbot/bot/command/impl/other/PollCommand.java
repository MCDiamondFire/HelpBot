package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.*;

public class PollCommand extends Command {


    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Creates a poll.")
                .category(CommandCategory.OTHER);
    }

    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        //hope i remember to change this to dev
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {

        ArrayList<String> pollOptions = new ArrayList<>();
        pollOptions.addAll(Arrays.asList(event.getMessage().getContentRaw().split("\\|")));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.valueOf(pollOptions.get(0).subSequence(6,pollOptions.get(0).length())));
        pollOptions.remove(0);
        builder.setDescription(String.join(" ", pollOptions));
        builder.setColor(new Color(33, 40, 97));

        event.getChannel().sendMessage(builder.build()).queue((message) -> {

            int len = pollOptions.toArray().length;

            for (int i = 0; i < len; i+= 2) {

                message.addReaction(pollOptions.get(i)).queue();

            }

        });

    }
}
