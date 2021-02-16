package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import java.util.Random;

public class IdeaCommand extends Command {
    
    @Override
    public String getName() {
        return "idea";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a genius game idea.")
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
        
        String[] types = new String[]{
                "A clicker game",
                "A building game",
                "A snowy game",
                "A survival game",
                "A parkour course",
                "A puzzle game",
                "A creative game",
                "A battle royale game",
                "A sandbox game",
                "A sunny game",
                "An extremely hot game",
                "An exploration game",
                "A dungeon game",
                "A grass game",
                "A Dutch game",
                
        };
        String[] objectives = new String[]{
                "where you adopt hamsters",
                "where you build structures",
                "where you build a snowman",
                "where you connect wires",
                "where you play Fortnite",
                "where you murder others",
                "where you act to jump",
                "where you disguise as a Lander",
                "where you chill out",
                "where you run for your life",
                "where you party with blocks",
                "where you punch flowers",
                "where you raid Area 51",
                "where you swipe Jeremaster to the left",
                "where you explore dungeons",
                "where you kill monsters",
                "where you attack the DiamondFire Moderator team",
                "where you send a love letter",
                "where you express love",
                "where you can't enter buildings",
        };
        String[] rewards = new String[]{
                "for pleasure.",
                "with RyanLand <3.",
                "with Jeremaster.",
                "for 50 tokens.",
                "for the next level.",
                "to unlock Player Action: Send Title.",
                "to unlock the Midas Touch cosmetic.",
                "for all colors of the rainbow.",
                "for coins.",
                "for gold.",
                "for grandma's cookies.",
                "to see ET.",
                "for a trip to Amsterdam.",
                "for a ticket to see Jeremaster's movie.",
                "for a lot of free kaasblokjes.",
        };
        
        Random rdm = new Random();
        EmbedBuilder builder = new EmbedBuilder();
            
                    builder.setTitle("Idea");
                    builder.setDescription(types[rdm.nextInt(types.length)] + " " + objectives[rdm.nextInt(objectives.length)] + " " + rewards[rdm.nextInt(rewards.length)]);
                
        event.getChannel().sendMessage(builder.build()).queue();
    }
    
}

