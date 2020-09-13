package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.util.function.BiConsumer;


public class CodeCommand extends AbstractSingleQueryCommand {

    public static <T extends CodeObject> void sendHelpMessage(T data, TextChannel channel) {
        EmbedBuilder builder = data.getEnum().getEmbedBuilder().generateEmbed(data);
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
        channel.sendMessage(builder.build()).addFile(actionIcon, material + ".png").queue();
    }

    @Override
    public String getName() {
        return "code";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information for a game value, code block or action. If you cannot find what you want, try using the ?search command.")
                .category(CommandCategory.CODE_BLOCK)
                .addArgument(
                        new HelpContextArgument()
                                .name("codeblock|action|game value")
                );
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
    public BiConsumer<CodeObject, TextChannel> onDataReceived() {
        return CodeCommand::sendHelpMessage;
    }
}
