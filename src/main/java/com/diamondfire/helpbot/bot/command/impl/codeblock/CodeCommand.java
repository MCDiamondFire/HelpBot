package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.handler.ReplyHandler;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.*;
import java.nio.file.Files;
import java.util.function.BiConsumer;


public class CodeCommand extends AbstractSingleQueryCommand {
    
    public static <T extends CodeObject> void sendHelpMessage(T data, ReplyHandler replyHandler) {
        EmbedBuilder builder = data.getEnum().getEmbedBuilder().generateEmbed(data);
        String customHead = data.getItem().getHead();
        
        if (customHead == null) {
            File actionIcon = Util.fetchMinecraftTextureFile(data.getItem().getMaterial().toUpperCase());
            builder.setThumbnail("attachment://" + actionIcon.getName());
            try {
                replyHandler.replyFile(builder, Files.readAllBytes(actionIcon.toPath()), actionIcon.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            builder.setThumbnail(customHead);
            replyHandler.reply(builder);
        }
        
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
    public BiConsumer<CodeObject, ReplyHandler> onDataReceived() {
        return CodeCommand::sendHelpMessage;
    }
}
