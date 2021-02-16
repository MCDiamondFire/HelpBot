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
                "A cultured game",
                "An old game",
                "A recreated game",
                "A friendly game",
                "An angry game",
                "A PVP game",
                "A procedurally generated game",
                "A simulator game",
                "A tower defence game",
                "A sandbox game",
                "A top-down game",
                "A first-person shooter",
                "An RPG game",
                "A Metroidvania game",
                "A rogue-lite game",
                "A casual game",
                "A farming game",
                "A city builder game",
                "A rhythm game",
                "A point & click game",
                "An arcade game",
                "A co-op game",
                "A stealth game",
                "A board game",
                "A text-based game",
                "A horror game",
                "An educational game",
                "A turn-based game",
                "A strategy game",
                "A bullet hell game",
                
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
                "where you dance",
                "where you are sus",
                "where you sing",
                "where you grill steak",
                "where you fight squids",
                "where you break the 4th wall",
                "where you make an unscripted rant",
                "where you craft items",
                "where you can't move",
                "where you can't jump",
                "where DFIdentity hunts you down",
                "where you control a dolphin",
                "where you need a key",
                "where you always swim",
                "where you read a Buzzfeed article",
                "where you become a robot",
                "where you need to escape",
                "where you shoot missiles",
                "where you watch TV",
                "where you wear gold armour",
                "where you are American",
                "where you are British",
                "where you fight in World War 2",
                "where you delete builds",
                "where you abuse glitches",
                "where you have half a heart",
                "where you have a midlife crisis",
                "where you spawn fiery diamonds",
                "where you mine a lot of redstone",
                "where you speedrun it",
                "where you have to leave the plot",
                "where you are staff",
                "where you break people's hearts",
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
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
        };
        
        Random rdm = new Random();
        EmbedBuilder builder = new EmbedBuilder();
            
                    builder.setTitle("Idea");
                    builder.setDescription(types[rdm.nextInt(types.length)] + " " + objectives[rdm.nextInt(objectives.length)] + " " + rewards[rdm.nextInt(rewards.length)]);
                
        event.getChannel().sendMessage(builder.build()).queue();
    }
    
}

