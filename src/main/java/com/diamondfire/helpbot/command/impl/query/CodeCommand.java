package com.diamondfire.helpbot.command.impl.query;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.command.permissions.PermissionHandler;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.components.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.instance.BotInstance;
import com.diamondfire.helpbot.util.BotConstants;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;


public class CodeCommand extends AbstractSingleQueryCommand {

    public static void sendHelpMessage(SimpleData data, TextChannel channel) {
        if (data == null) return;

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
        return "code";
    }

    @Override
    public String getDescription() {
        return "Gets information for a game value, code block or action. If you cannot find what you want, try using the ?search command.";
    }

    @Override
    public ValueArgument<String> getArgument() {
        return new StringArg("Codeblock/Action/GameValue Name", true);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        super.run(event);
    }

    @Override
    public BiConsumer<SimpleData, TextChannel> onDataReceived() {
        return CodeCommand::sendHelpMessage;
    }
}
