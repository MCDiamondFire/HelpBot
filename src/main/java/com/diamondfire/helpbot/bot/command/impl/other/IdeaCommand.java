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
                "A skyblock game",
                "An educational game",
                "A generic wire game",
                "A grindy game"
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
                "where you rap like a god",
                "where Ottelino rejoins the discord yet again",
                "where you join the events team",
                "where you get Discord Nitro",
                "where you post clips",
                "where you contribute to HelpBot",
                "where Fejiberglibstein gives you big crazies",
                "where Refriz says uwu",
                "where you request support",
                "where you ban people",
                "where you post memes",
                "where you go on a DiamondFire rant",
                "where you perform big codes",
                "where you break HelpBot",
                "where you do the RyanLand",
                "where you become a redditor",
                "where you retire",
                "where you generate samquotes",
                "where you accidentally the cat",
                "where Owen posts another strange video",
                "where you admire hedgehogs",
                "where you wear a mask",
                "where you set a computer on fire",
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
                "for diamonds straight from the mine.",
                "for Owen's cookies.",
                "for admin.",
                "for the memes.",
                "for a lot of exposure on Wall Street.",
                "for a gift with chocolate.",
                "to unlock a new cosmetic.",
                "for a large shoutout.",
                "for an exclusive gadget.",
                "to receive a gangster drip.",
                "for a concert with Eminem.",
                "for a private concert with MrMine05.",
                "for money.",
                "for a ban from DiamondFire.",
                "for a Kick of Truth from lukecashwell.",
                "for a big prize.",
                "for an exclusive BuzzFeed article.",
                "for Develoepr.",
                "with a turtle.",
                "with friends.",
                "with a very big elephant.",
                "for a small compliment.",
                "for a troll.",
                "for the fall of death.",
                "for a visit to prison.",
                "to visit McDonalds.",
                "for a big McChicken burger.",
                "for an egg from a chicken.",
                "for another plot.",
                "for a neat firework show.",
                "to listen to Pigstep.",
                "for a show from DJ RyanLand.",
                "for a bunch of ducks saying quack.",
                "for good game ideas.",
                "for a volcano eruption.",
                "for the new DiamondFire update.",
                "to get banned.",
                "for samquotes.",
                "for love.",
                "for Robot Game levels.",
                "for Hypercube source code.",
                "for warm blankets.",
                "for the labsCore API.",
                "for V-Bucks.",
                "to get hugs from Owen.",
                "to eat food.",
                "for red pandas",
                "to avoid COVID-19",
        };
        
        Random rdm = new Random();
        EmbedBuilder builder = new EmbedBuilder();
        
        builder.setTitle("Idea");
        builder.setDescription(types[rdm.nextInt(types.length)] + " " + objectives[rdm.nextInt(objectives.length)] + " " + rewards[rdm.nextInt(rewards.length)]);
        
        event.getChannel().sendMessage(builder.build()).queue();
    }
    
}

