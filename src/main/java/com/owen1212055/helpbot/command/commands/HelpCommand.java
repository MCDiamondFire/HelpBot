package com.owen1212055.helpbot.command.commands;

import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.LazyStringArg;
import com.owen1212055.helpbot.command.commands.query.AbstractSingleQueryCommand;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.command.permissions.PermissionHandler;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.events.CommandEvent;
import com.owen1212055.helpbot.instance.BotInstance;
import com.owen1212055.helpbot.util.BotConstants;
import com.owen1212055.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.util.function.BiConsumer;


public class HelpCommand extends AbstractSingleQueryCommand {
    public static void sendHelpMessage(SimpleData data, TextChannel channel) {
        if (data == null) {
            return;
        }
        EmbedBuilder builder = data.getEnum().getEmbedBuilder().generateEmbed(data);
        String material;
        File actionIcon;

        File customHead = data.getItem().getHead();
        if (customHead == null) {
            material = data.getItem().getMaterial().toLowerCase();

            actionIcon = Util.fetchMinecraftTextureFile(material);

        } else {
            actionIcon = customHead;
            material = customHead.getName();
        }
        builder.setThumbnail("attachment://" + material + ".png");

        channel.sendMessage(builder.build()).addFile(actionIcon, material + ".png").queue();

    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Gets information for a game value, code block or action. If you cannot find what you want, try using the ?search command. \nSpecifying no arguments causes this help menu to appear.";
    }

    @Override
    public Argument getArgument() {
        return new LazyStringArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        if (event.getArguments().length == 0) {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Help");
            builder.setDescription("HelpBot is a bot dedicated to looking at information regarding game values, codeblocks, actions, and more! Listed below are the commands that you are currently allowed to use. Any additional questions may be forwarded to Owen1212055");
            builder.setThumbnail(BotInstance.getJda().getSelfUser().getAvatarUrl());
            builder.setFooter("Your permissions: " + PermissionHandler.getPermission(event.getMember()));

            BotInstance.getHandler().getCommands().values().stream()
                    .filter(Command::inHelp)
                    .filter((command) -> command.getPermission().hasPermission(event.getMember()))
                    .forEach(command -> builder.addField(BotConstants.PREFIX + command.getName() + " " + command.getArgument(), command.getDescription(), false));

            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }
        super.run(event);


    }

    @Override
    public BiConsumer<SimpleData, TextChannel> onDataReceived() {
        return HelpCommand::sendHelpMessage;
    }
}
