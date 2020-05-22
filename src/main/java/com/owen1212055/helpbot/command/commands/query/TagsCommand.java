package com.owen1212055.helpbot.command.commands.query;

import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.BasicStringArg;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.CodeBlockActionData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.CodeBlockData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.CodeBlockTagData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.events.CommandEvent;
import com.owen1212055.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.util.function.BiConsumer;


public class TagsCommand extends AbstractSingleQueryCommand {
    public static void sendTagMessage(SimpleData data, TextChannel channel) {
        if (data == null) {
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();
        CodeBlockActionData actionData;
        if (data instanceof CodeBlockData && ((CodeBlockData) data).getAssociatedAction() != null) {
            actionData = ((CodeBlockData) data).getAssociatedAction();

        } else if (data instanceof CodeBlockActionData) {
            actionData = (CodeBlockActionData) data;
        } else {
            builder.setTitle("Invalid data!");
            builder.setDescription("What you have searched for is not a valid action!");
            channel.sendMessage(builder.build()).queue();
            return;
        }

        if (actionData.getTags().length == 0) {
            builder.setTitle("No tags!");
            builder.setDescription("This action does not contain any tags!");
            channel.sendMessage(builder.build()).queue();
            return;
        }

        for (CodeBlockTagData tag : actionData.getTags()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String option : tag.getOptions()) {
                stringBuilder.append("\n\\> " + (option.equals(tag.getDefaultValue()) ? String.format("**%s**", option) : option));
            }
            builder.addField(tag.getName(), stringBuilder.toString(), true);
        }

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


        builder.setTitle("Tags for: " + data.getMainName());
        channel.sendMessage(builder.build()).addFile(actionIcon, material + ".png").queue();

    }

    @Override
    public String getName() {
        return "tags";
    }

    @Override
    public String getDescription() {
        return "Gives you to get a list of tags for a code block or action.";
    }

    @Override
    public Argument getArgument() {
        return new BasicStringArg();
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
        return TagsCommand::sendTagMessage;
    }
}
