package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.*;

import java.io.File;
import java.util.function.*;


public class CodeCommand extends AbstractSingleQueryCommand {
    
    public static <T extends CodeObject> void sendHelpMessage(T data, Consumer<MessageCreateData> reply) {
        EmbedBuilder builder = data.getEnum().getEmbedBuilder().generateEmbed(data);
        String customHead = data.getItem().getHead();
        File actionIcon = customHead == null ? Util.fetchMinecraftTextureFile(data.getItem().getMaterial().toUpperCase()) : null;
        
        Thumbnail thumbnail = actionIcon == null ? Thumbnail.fromUrl(customHead) : Thumbnail.fromFile(FileUpload.fromData(actionIcon));
        Container container = ComponentUtil.toContainer(builder.build(), thumbnail);
        if (container != null) {
            reply.accept(new MessageCreateBuilder().useComponentsV2().setComponents(container).build());
            return;
        }
        
        if (actionIcon != null) {
            builder.setThumbnail("attachment://" + actionIcon.getName());
            reply.accept(new MessageCreateBuilder().setEmbeds(builder.build()).setFiles(FileUpload.fromData(actionIcon)).build());
        } else {
            builder.setThumbnail(customHead);
            reply.accept(MessageCreateData.fromEmbeds(builder.build()));
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
    public void run(CommandEvent event) {
        super.run(event);
    }
    
    @Override
    public BiConsumer<CodeObject, Consumer<MessageCreateData>> onDataReceived() {
        return CodeCommand::sendHelpMessage;
    }
}
