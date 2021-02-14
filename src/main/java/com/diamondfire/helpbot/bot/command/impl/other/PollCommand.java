package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

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
                .category(CommandCategory.OTHER);
    }

    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER; //should be DEVELPOER, USER for testing
    }

    @Override
    public void run(CommandEvent event) {

        //sends arguments on a ?poll command
        if (event.getMessage().getContentRaw().equals(getConfig().getPrefix() + "poll")) {
    
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Poll Command");
            error.setDescription("The arguments for the poll command are:\n" + getConfig().getPrefix() + "poll [Question]\\|[Option 1]\\|[Option 2]\\|");
            error.setColor(Color.RED);
    
            event.getChannel().sendMessage(error.build()).queue();
            
            return;
        }
        
        ArrayList<String> pollOptions = new ArrayList<>();
        pollOptions.addAll(Arrays.asList(event.getMessage().getContentRaw().split("\\|"))); //poll options + poll question (contains ?poll)
        String pollQuestion = String.valueOf(pollOptions.get(0).subSequence(6,pollOptions.get(0).length())); //poll question (does not contain ?poll)
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
    
        int len = pollOptions.toArray().length; //get amount of poll options
        
        //checks if there are no arguments
        if (len == 0) {
    
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle("Error!");
            error.setDescription("The arguments are incorrect. Correct arguments are:\n" + getConfig().getPrefix() + "poll [Question]\\|[Option 1]\\|[Option 2]\\|");
            error.setColor(Color.RED);
    
            event.getChannel().sendMessage(error.build()).queue();
            
            return;
        }
        
        event.getChannel().sendMessage(builder.build()).queue((message) -> { //send embed message
    
            //add reactions
            Deque<String> nums = Util.getUnicodeNumbers();
            for (String option : pollOptions) {
                message.addReaction(nums.pop()).queue();
            }

        });
        
        //delete original message
        event.getMessage().delete().queue();

    }
}
