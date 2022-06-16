package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.command.reply.handler.followup.FollowupReplyHandler;
import com.diamondfire.helpbot.bot.events.command.*;
import com.diamondfire.helpbot.util.nbs.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class NbsCommand extends Command {
    
    private static final String NBS_IMAGE = "https://static.wikia.nocookie.net/minecraft/images/9/9b/Note_Block.png/revision/latest?cb=20190921170620";
    
    @Override
    public String getName() {
        return "nbs";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a Codeutils song function.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public boolean supportsSlashCommands() {
        return false;
    }
    
    @Override
    protected ArgumentSet compileArguments() { return new ArgumentSet(); }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        MessageCommandEvent messageCommandEvent = (MessageCommandEvent) event;
        
        PresetBuilder nbsPreset = new PresetBuilder()
            .withPreset(new InformativeReply(InformativeReplyType.ERROR,"You need to attach an nbs file!"));
        PresetBuilder error = new PresetBuilder()
            .withPreset(new InformativeReply(InformativeReplyType.ERROR,"Something went wrong while processing/generating!"));
        
        Message.Attachment attachment = messageCommandEvent.getMessage().getAttachments().get(0);
        if(attachment == null || !Objects.equals(attachment.getFileExtension(), "nbs")) {
            event.reply(nbsPreset);
            return;
        }
    
        event.getReplyHandler().deferReply(new PresetBuilder().withPreset(
                new InformativeReply(InformativeReplyType.INFO, "Please wait while your nbs file is converted,")
        ))
                .thenAccept(followupReplyHandler -> {
                    attachment.retrieveInputStream().thenAccept(stream -> {
                        try {
                            byte[] b64 = new NBSToTemplate(NBSDecoder.parse(stream, attachment.getFileName())).convert();
                            String fileContent = String.format("/give @p minecraft:ender_chest{display:{Name:'[{\"text\":\"» \",\"color\":\"gold\"},{\"text\":\"Code Template\",\"color\":\"yellow\",\"bold\":true}]'},PublicBukkitValues:{\"hypercube:codetemplatedata\":'{\"name\":\"&6» &e&lCode Template\",\"version\":1,\"code\":\"%s\",\"author\":\"helpbot\"}'}} 1", new String(b64));
                            
                            EmbedBuilder embed =
                                    new PresetBuilder().withPreset(new InformativeReply(
                                            InformativeReplyType.INFO,
                                            "You can copy the command below and give it to yourself in singleplayer, then use saved toolbars to transfer it to DiamondFire. You will need a [Music Player](https://derpystuff.gitlab.io/code/l?link=7cf5d91c35bbde31c28567d8d8945c40) function to play this song!"
                                    ))
                                    .getEmbed()
                                    .setTitle("Template Generated!")
                                    .setThumbnail(NBS_IMAGE);
                            
                            followupReplyHandler.editOriginalFile(embed, fileContent.getBytes(StandardCharsets.UTF_8), "nbs_output.txt");
                        } catch (OutdatedNBSException | IOException e) {
                            e.printStackTrace();
                            event.reply(error);
                        }
                    });
                });
    }
    
}
