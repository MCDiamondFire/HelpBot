package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.textgen.SamQuotes;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;

public class SubmitSamquoteSubCommand extends SubCommand {
    @Override
    public String getName() {
        return "submit";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .addArgument(
                        new HelpContextArgument().name("channel"),
                        new HelpContextArgument().name("message id")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("channel_id", DiscordMentionArgument.channel())
                .addArgument("message_id", new LongArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        
        long channelID = event.getArgument("channel_id");
        long messageID = event.getArgument("message_id");
    
        TextChannel channel = event.getGuild().getTextChannelById(channelID);
        if (channel == null) {
            event.reply(new PresetBuilder()
                    .withPreset(new InformativeReply(InformativeReplyType.ERROR, "Unknown channel!")));
            return;
        }
        
        channel.retrieveMessageById(messageID).queue((messageText) -> {
            if(messageText.getAuthor().getIdLong() == 132092551782989824L) {
                
                try {
                    String text = messageText.getContentRaw().replaceAll("[^a-zA-Z0-9 ]", "");
                    BufferedImage combined = SamImage.createFull(text);
                    
                    //save image
                    Path imageFile = ExternalFiles.SAM_DIR.resolve(text.replaceAll(" ", "") + ".png");
                    ImageIO.write(combined, "PNG", imageFile.toFile());
                    
                    event.reply(new PresetBuilder()
                            .withPreset(new InformativeReply(InformativeReplyType.SUCCESS, "Your samquote has been added!")));
                    
                    SamQuotes.add(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            } else {
                event.reply(new PresetBuilder()
                        .withPreset(new InformativeReply(InformativeReplyType.ERROR, "This is not a samquote!")));
                
            }
        });
    }
}
