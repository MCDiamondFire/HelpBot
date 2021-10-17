package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class CowsayCommand extends Command {
    
    @Override
    public String getName() {
        return "cowsay";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext().description("generates ASCII art pictures of a cow with a message")
                .category(CommandCategory.OTHER)
                .addArgument(new HelpContextArgument()
                        .name("message"));
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument("message", new StringArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        String message = event.getArgument("message");
        
        char bubbleTop = '_';
        char bubbleBot = '-';
        
        StringBuilder bubbleBuilder = new StringBuilder();
        
        // Generates the top closing part of the speech bubble
        bubbleBuilder.append(bubbleTop);
        bubbleBuilder.append(String.valueOf(bubbleTop).repeat(Math.max(0, message.length() + 1)));
        bubbleBuilder.append(bubbleTop + "\n");
        
        bubbleBuilder.append("< " + message + " >\n"); // Generates the middle and message part of the speech bubble
        
        bubbleBuilder.append(bubbleBot);
        bubbleBuilder.append(String.valueOf(bubbleBot).repeat(Math.max(0, message.length() + 1)));
        bubbleBuilder.append(bubbleBot + "\n");
        
        StringBuilder cowBuilder = new StringBuilder();
        cowBuilder.append("        \\   ^__^\n");
        cowBuilder.append("         \\  (oo)\\_______\n");
        cowBuilder.append("            (__)\\       )\\/\\\n");
        cowBuilder.append("                ||----w |\n");
        cowBuilder.append("                ||     ||\n");
    
    
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("The cow says")
                .appendDescription("```\n" + bubbleBuilder.toString() + cowBuilder.toString() + "\n```");
        
        event.getMessage().getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
