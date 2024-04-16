package com.diamondfire.helpbot.bot.command.impl.other.util;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MessageArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;
import java.util.*;

import static com.diamondfire.helpbot.bot.HelpBotInstance.getConfig;

public class PollCommand extends Command {
    
    
    @Override
    public String getName() {
        return "poll";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Creates a poll.")
                .category(CommandCategory.OTHER).
                        addArgument(new HelpContextArgument()
                                .name("text|input"));
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().
                addArgument("input",
                        new MessageArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }
    
    @Override
    public void run(CommandEvent event) {
        String input = event.getArgument("input");
        //sends arguments on a ?poll command
        if (input.isEmpty()) {
            PresetBuilder preset = new PresetBuilder();
            preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Poll Command", "The arguments for the poll command are:\n" + getConfig().getPrefix() + "poll [Question]\\|[Option 1]\\|[Option 2]\\|"));
            event.reply(preset);
            return;
        }
        
        ArrayList<String> pollOptions = new ArrayList<>();
        pollOptions.addAll(Arrays.asList(input.split("\\|"))); //poll options + poll question
        
        //checks if there are no arguments
        if (pollOptions.isEmpty()) {
            PresetBuilder preset = new PresetBuilder();
            preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "The arguments are incorrect. Correct arguments are:\\n" + getConfig().getPrefix() + "poll [Question]\\\\|[Option 1]\\\\|[Option 2]\\\\|"));
            event.reply(preset);
            return;
        }
        
        
        String pollQuestion = pollOptions.get(0); //poll question
        pollOptions.remove(0); //remove the poll question so only the options remain
        
        ArrayList<String> fullPollOptions = new ArrayList<>();
        fullPollOptions.addAll(pollOptions);
        
        int i = 0;
        Deque<String> unicodeChars = Util.getUnicodeNumbers();
        
        //create a list with both emotes and poll options
        for (String option : pollOptions) {
            
            if (i > fullPollOptions.toArray().length) {
                
                //prevent an out of bounds error
                fullPollOptions.add("\n" + unicodeChars.pop());
                
            } else {
                
                fullPollOptions.add(i, "\n" + unicodeChars.pop());
                
            }
            
            i += 2;
        }
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(pollQuestion); //sets title to poll question
        builder.setDescription(String.join(" ", fullPollOptions)); //adds poll options
        builder.setColor(new Color(33, 40, 97));
        
        event.getChannel().sendMessageEmbeds(builder.build()).queue((message) -> { //send embed message
            
            //add reactions
            Deque<String> nums = Util.getUnicodeNumbers();
            for (String option : pollOptions) {
                message.addReaction(Emoji.fromUnicode(nums.pop())).queue();
            }
            
        });
        
        //delete original message
        event.getMessage().delete().queue();
        
    }
}
