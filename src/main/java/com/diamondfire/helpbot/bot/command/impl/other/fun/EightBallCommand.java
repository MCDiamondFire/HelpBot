package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Random;

public class EightBallCommand extends Command {
    
    @Override
    public String getName() {
        return "8ball";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"8b"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Helps make decisions.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("question")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("question",
                        new SingleArgumentContainer<>(new MessageArgument()));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        
        String[] responses = new String[]{
                "No.",
                "Yes.",
                "Maybe..",
                "Definitely!",
                "How about no..",
                "Good question..",
                "Sure why not",
                "Go for it!",
                "Without a doubt.",
                "Most likely.",
                "Very doubtful.",
                "Brilliant idea!",
                //"Ask owen\nDM: <@246778942323818506>\nPING: <@246778942323818506>",
                "Mhmm..",
        };
        
        Random rdm = new Random();
        EmbedBuilder builder = new EmbedBuilder();
        
        builder.setTitle(":8ball: | " + event.getArgument("question"));
        builder.setDescription(responses[rdm.nextInt(responses.length)]);
        
        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
    
}

