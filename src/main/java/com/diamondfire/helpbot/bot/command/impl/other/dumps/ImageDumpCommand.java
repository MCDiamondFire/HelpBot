package com.diamondfire.helpbot.bot.command.impl.other.dumps;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.IOUtil;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.*;

public class ImageDumpCommand extends Command {
    
    @Override
    public String getName() {
        return "imagedump";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Provides over a zip file containing all of the minecraft photos HelpBot uses. Each image is 300x300 and is rendered the exact same way it would in Minecraft.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        File images = ExternalFiles.IMAGES_DIR;
        PresetBuilder builder = new PresetBuilder();
        builder.withPreset(new InformativeReply(InformativeReplyType.INFO, "Please wait, the zip is being created."));
        event.getReplyHandler().replyA(builder).queue((msg) -> {
            try {
                File zip = IOUtil.zipFile(images.toPath(), "images.zip");
                event.getChannel().sendFiles(FileUpload.fromData(zip)).queue((fileMsg) -> {
                    msg.delete().queue();
                });
            } catch (IOException e) {
                PresetBuilder builderError = new PresetBuilder();
                builder.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Failed to send zip file."));
                msg.editMessageEmbeds(builderError.getEmbed().build()).queue();
            }
        });
        
    }
    
}


