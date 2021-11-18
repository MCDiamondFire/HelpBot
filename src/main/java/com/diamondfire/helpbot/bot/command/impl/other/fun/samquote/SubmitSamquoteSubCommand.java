package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.LongArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.commands.*;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.sys.samquote.SamImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class SubmitSamquoteSubCommand extends SubCommand {
    
    @Override
    public String getName() {
        return "submit";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .addArgument(
                        new HelpContextArgument().name("channel_id"),
                        new HelpContextArgument().name("message_id")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("channel_id", new LongArgument())
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
    
        event.getGuild().getTextChannelById(channelID).retrieveMessageById(messageID).queue((messageText) -> {
        
            if(messageText.getAuthor().getIdLong() == 132092551782989824L) {
            
                try {
                
                    String text = "           " + messageText.getContentRaw().replaceAll("[^a-zA-Z0-9 ]", "");
                
                    BufferedImage combined = SamImage.createFull(text);
                
                    //save image
                
                    File imageFile = new File(ExternalFiles.SAM_DIR, messageText.getContentRaw().replaceAll("[^a-zA-Z0-9]", "") + ".png");
                    ImageIO.write(combined, "PNG", imageFile);
                
                    PresetBuilder success = new PresetBuilder();
                
                    success.withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS, "Your SamQuote has been added!")
                    );
                
                    event.reply(success);
                
                    addSamquote(messageText.getContentRaw().replaceAll("[^a-zA-Z0-9]", ""));
                
                } catch (IOException e) {
                
                    e.printStackTrace();
                
                }
            
            } else {
            
                PresetBuilder error = new PresetBuilder();
            
                error.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "This is not a samquote!")
                );
            
                event.reply(error);
            
            }
        
        });
    }
    
    protected static void addSamquote(String samquote) {
        try {
            
            File file = new File("samquotes.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            String line;
            StringBuilder newFile = new StringBuilder();
            
            while ((line = br.readLine()) != null) {
                
                if (!line.equals(samquote)) { newFile.append(line).append("\n"); }
            }
            
            newFile.append(samquote);
            
            FileWriter fileWriter = new FileWriter("samquotes.txt");
            fileWriter.write(newFile.toString());
            fileWriter.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
