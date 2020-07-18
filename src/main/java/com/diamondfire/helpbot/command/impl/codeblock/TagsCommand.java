package com.diamondfire.helpbot.command.impl.codeblock;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.util.function.BiConsumer;


public class TagsCommand extends AbstractSingleQueryCommand {

    private static void sendTagMessage(SimpleData data, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        CodeBlockActionData actionData;

        // Handle codeblocks that have actions associated with them.
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
            material = data.getItem().getMaterial().toUpperCase();
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
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Lists all tags that a certain codeblock has.")
                .category(CommandCategory.CODE_BLOCK)
                .addArgument(
                        new HelpContextArgument()
                                .name("action name")
                );
    }

    @Override
    public String getName() {
        return "tags";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"codetags", "tag"};
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
