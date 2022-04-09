package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static com.diamondfire.helpbot.util.textgen.MarkovManipulation.getNextWord;

public class GenerateSamquoteSubCommand extends SubCommand {
    @Override
    public String getName() {
        return "generate";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a fake (but real sounding) samquote.")
                .addArgument(new HelpContextArgument().name("starting word").optional());
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("start_word", new SingleArgumentContainer<>(new StringArgument()).optional(null));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        File file = new File("samquotes.txt");
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        ArrayList<String> startingTexts = new ArrayList<>();
        
        String line = null;
        while (true) {
            try {
                if ((line = br.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            startingTexts.add(line.split(" ")[0]);
        }
        
        String word;
        
        String startWord = event.getArgument("start_word");
        if (startWord == null) {
            word = startingTexts.get((int) Math.rint(Math.random() * startingTexts.size()));
        } else {
            if (startingTexts.contains(startWord)) {
                word = startWord;
            } else {
                PresetBuilder error = new PresetBuilder();
                
                error.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Sam has never started a message with this word before!")
                );
                
                return;
            }
        }
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            
            if (word == null || word.equals(".")) break;
            string.append(word).append(" ");
            try {
                word = getNextWord(word);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(SamImage.createFull(string.toString()), "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            event.reply(new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR, "Unable to create samquote image.")));
            return;
        }
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Sam Quote");
        builder.setImage("attachment://quote.png");
        builder.setColor(new Color(87, 177, 71));
        
        event.getChannel().sendMessageEmbeds(builder.build()).addFile(baos.toByteArray(), "quote.png").queue();
    }
}
