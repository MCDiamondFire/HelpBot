package com.diamondfire.helpbot.bot.command.impl.other.dumps;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.*;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.IOUtil;

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
        event.getReplyHandler()
                .deferReply(new PresetBuilder().withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Please wait, the zip is being created.")))
                .thenAccept(followupReplyHandler -> {
                    File zip = createZip();
                    if (zip != null) {
                        followupReplyHandler.editOriginalFile("", zip, "images.zip");
                    } else {
                        followupReplyHandler.editOriginal(createErrorPreset());
                    }
                });
    }
    
    private File createZip() {
        try {
            return IOUtil.zipFile(ExternalFiles.IMAGES_DIR.toPath(), "images.zip");
        } catch (IOException e) {
            return null;
        }
    }
    
    private PresetBuilder createErrorPreset() {
        PresetBuilder builderError = new PresetBuilder();
        builderError.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Failed to send zip file."));
        return builderError;
    }
    
}


