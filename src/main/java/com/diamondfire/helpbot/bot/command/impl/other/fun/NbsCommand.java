package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.nbs.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.*;

public class NbsCommand extends Command {
    
    private static final String NBS_IMAGE = "https://static.wikia.nocookie.net/minecraft/images/9/9b/Note_Block.png/revision/latest?cb=20190921170620";
    
    @Override
    public String getName() {
        return "nbs";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a Recode song function.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        GuildMessageChannel channel = event.getChannel().asGuildMessageChannel();
        PresetBuilder attachNbsMsg = new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR,"You need to attach an nbs file!"));
        PresetBuilder errorMsg = new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR,"Something went wrong while generating!"));
        
        if (event.getMessage().getAttachments().isEmpty()) {
            event.reply(attachNbsMsg);
            return;
        }
        
        Message.Attachment attachment = event.getMessage().getAttachments().get(0);
        if (!attachment.getFileExtension().equals("nbs")) {
            event.reply(attachNbsMsg);
            return;
        }
        
        File file = new File("input.nbs");
        
        
        attachment.downloadToFile(file).thenAccept(downloadedFile -> {
            try {
                byte[] b64 = new NBSToTemplate(NBSDecoder.parse(file)).convert();
                File templateOutputfile = File.createTempFile("nbs_output", ".txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(templateOutputfile));
                writer.write(String.format("/give @p minecraft:ender_chest{display:{Name:'[{\"text\":\"» \",\"color\":\"gold\"},{\"text\":\"Code Template\",\"color\":\"yellow\",\"bold\":true}]'},PublicBukkitValues:{\"hypercube:codetemplatedata\":'{\"name\":\"&6» &e&lCode Template\",\"version\":1,\"code\":\"%s\",\"author\":\"helpbot\"}'}} 1", new String(b64)));
                
                writer.close();
                
                    
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(70,199,82))
                        .setTitle("Template Generated!")
                        .setThumbnail(NBS_IMAGE)
                        .addField("Information","You can copy the command above and give it to yourself in singleplayer. Use toolbars to transfer it to Diamondfire. You will need a [Music Player](https://dfonline.dev/edit/?template=nbs) function to play this song!", false);
                
                
                channel.sendFiles(FileUpload.fromData(templateOutputfile)).setEmbeds(embed.build()).queue();
                file.deleteOnExit();
            } catch (OutdatedNBSException | IOException e) {
                e.printStackTrace();
                event.reply(errorMsg);
            }
        });
    }
    
}
