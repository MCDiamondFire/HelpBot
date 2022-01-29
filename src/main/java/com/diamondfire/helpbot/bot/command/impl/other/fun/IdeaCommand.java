package com.diamondfire.helpbot.bot.command.impl.other.fun;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public class IdeaCommand extends Command {
    
    private static final int[] VALUES = new int[]{1,2,3,4};
    private static final int[] WEIGHTS = new int[]{10,5,2,1};
    
    private static final String[] GAME_TYPES = new String[]{ // For the main genre of the game. 'A(n) <x>, <x> game.'
            "2D",
            "Adventure",
            "Anarchy",
            "Arcade",
            "Arena",
            "Battle-Royale",
            "Board",
            "Boss-Fight",
            "Building",
            "Bullet-Hell",
            "Capture-the-Monument",
            "Card",
            "City",
            "Clicker",
            "Coding",
            "Creative",
            "Deck-Building",
            "Dungeon",
            "Elimination",
            "Escape",
            "Exploration",
            "Factions",
            "Farming",
            "Fighting",
            "GUI-Based",
            "Grinder",
            "Hack-and-Slash",
            "Heist",
            "Horror",
            "Infection",
            "King-of-the-Hill",
            "Magic",
            "Metroidvania",
            "Military",
            "Minigame",
            "Mining",
            "Parkour",
            "Parody",
            "Party",
            "Platformer",
            "Point-and-Click",
            "Puzzle",
            "PvE",
            "PvP",
            "RPG",
            "Racing",
            "Recreation",//e.g.Terraria or Quiplash or Pictionary or Doom or etc...
            "Rhythm",
            "Roguelike",
            "Roleplay",
            "Round-Based",
            "Sandbox",
            "Shooter",
            "Simulator",
            "Sky-Based",
            "Stealth",
            "Story-Driven",
            "Strategy",
            "Survival",
            "Technical/Demo",
            "Text-Based",
            "Top-Down",
            "Tower-Defense",
            "Trivia/Quiz",
            "Turn-Based",
            "Versus",
            "Visual-Novel"
    };
    
    private static final String[] ADJECTIVES = new String[] {
            "a Joke",
            "Action-Packed",
            "Casual",
            "Challenging",
            "Cheerful",
            "Colorful",
            "Competitive",
            "Complex",
            "Creepy",
            "Dynamic",  // Make what you will of this
            "Easy",
            "Enjoyable",
            "Fast-Paced",
            "Finite",
            "Friendly",
            "Frustrating",
            "Funny",
            "Grindy",
            "In-Depth",
            "Infinite",
            "Long",
            "Meta",
            "Multiplayer",
            "Musical",
            "Mysterious",
            "Passive",
            "Quality over Quantity",
            "Quantity over Quality",
            "Repetitive",
            "Retro",
            "Serious",
            "Short",
            "Simple",
            "Singleplayer",
            "Story Driven",
            "Thought Provoking",
            "Unique",
            "Unreasonably Polished",
            "Weird"
    };
    
    private static final String[] GAMEPLAY_ELEMENTS = new String[] {
            "a Large Scale",
            "a Skill Tree",
            "a Small Scale",
            "an aggressive amount of bees",
            "Abilities", "Abilities", // More weight
            "Achievements",
            "Aesthetics",
            "Air",
            "Animals",
            "Arson",
            "Artificial Intelligence",
            "Blindness",
            "Blocks",
            "Building",
            "Cars",
            "Chance",
            "Chat",
            "Clicking",
            "Close Combat",
            "Co-op",
            "Codes",
            "Color",
            "Combat",
            "Combinations",
            "Competition",
            "Cosmetics",
            "Crafting",
            "Creepers",
            "Currency",
            "Customizability",
            "Daily Content",
            "Darkness",
            "Day and Night",
            "Death",
            "Delivery",
            "Depth",
            "Dialogue",
            "Dice",
            "Dimensions",
            "Discovery",
            "Duels",
            "Eldritch Creatures",
            "Enemies", "Enemies",
            "Entities",
            "Equipment",
            "Factions",
            "Fire",
            "Flying",
            "Food",
            "Free for All",
            "Generation",
            "Gravity",
            "Guns",
            "Health",
            "Hell",
            "Hidden Areas",
            "Hunger",
            "Instructions",
            "Knowledge",
            "Lava",
            "Light",
            "Lootboxes",
            "Lore",
            "Magic",
            "Mazes",
            "Memes",
            "Minigames",
            "Missiles",
            "Mobility",
            "Mobs", "Mobs",
            "Money",
            "Monsters",
            "Movement",
            "Music",
            "NPCs",
            "Numbers",
            "Permanent Progress", "Permanent Progress",
            "Perspective",
            "Plants",
            "Power",
            "Progression",
            "Questionable Activities",
            "Randomly Generated Content",
            "Randomness",
            "Riddles",
            "Robots",
            "SANS?!?!?!", //SANS?!?!?!
            "Secrets",
            "Shops",
            "Skills",
            "Snow/Ice",
            "Sound",
            "Space",
            "Spells",
            "Stats",
            "Stealth",
            "Strategy",
            "Surviving",
            "Team Fights",
            "Teams",
            "Technology",
            "Teleportation",
            "Terrain",
            "the End",
            "the Sky",
            "Thinking",
            "Time",
            "Time Limits",
            "Unlocks",
            "Upgrades",
            "Variation",
            "Volcanoes",
            "Waiting",
            "Water"
    };
    
    private static final String[] THEMES = new String[] {
            "Ancient",
            "Automatic",
            "Broken",
            "Cavernous",
            "City",
            "Construct",
            "Dark",
            "Decay",
            "Deceptive",
            "Deleted",
            "Depths",
            "Desert",
            "Dimensional",
            "Fantasy",
            "Freedom",
            "Futuristic",
            "Gigantic",
            "Glitchy",
            "Growth",
            "Isolated",
            "Laboratory",
            "Linked",
            "Lush",
            "Magical",
            "Mirrored",
            "Modern",
            "Monochromatic",
            "Mythical",
            "Night",
            "Plain",
            "Power",
            "Remote",
            "Ruined",
            "Science",
            "Shattered",
            "Simplistic",
            "Snowy",
            "Space",
            "Spy",
            "Surreal",
            "Swarm",
            "Torment",
            "Underground",
            "Underwater",
            "Vanish",
            "Zombie"
    };
    
    private static final Random RNG = new Random();
    
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
    
    private int weightedRandom(int[] values, int[] weights){
        int totalWeight = 0;
        for (int weight : weights){
            totalWeight += weight;
        }
        int point = RNG.nextInt(totalWeight);
        for (int i = 0; i < weights.length; i++){
            if (point < weights[i]){
                return values[i];
            }
            point -= weights[i];
        }
        throw new IllegalStateException("Weighted Random improperly formatted.");
    }
    
    //private String getRngStrings(String[] sourceStrings, int numToFind){ return getRngStrings(sourceStrings, numToFind, " "); }
    private String getRngStrings(String[] sourceStrings, int numToFind, String joiner){
        List<String> strings = new ArrayList<>();
        String toAdd;
        while (numToFind > 0){
            toAdd = sourceStrings[RNG.nextInt(sourceStrings.length)];
            if (!strings.contains(toAdd)){
                strings.add(toAdd);
                numToFind--;
            }
        }
        return String.join(joiner, strings);
    }
    
 
    @Override
    public void run(CommandEvent event) {

        int numTypes = weightedRandom(VALUES, WEIGHTS);
        int numGameplay = weightedRandom(VALUES, WEIGHTS);
        
        String adj = RNG.nextInt(3) == 0 ? ADJECTIVES[RNG.nextInt(ADJECTIVES.length)] : null;
        String theme = RNG.nextInt(3) == 0 ? THEMES[RNG.nextInt(THEMES.length)] : null;
        String typeString = "A(n) " + getRngStrings(GAME_TYPES, numTypes, " ") + " game.";
        String gameplayString = "It should include " + getRngStrings(GAMEPLAY_ELEMENTS, numGameplay, ", ") +
                " and an emphasis on " + GAMEPLAY_ELEMENTS[RNG.nextInt(GAMEPLAY_ELEMENTS.length)] + ".";
        
        String finalString = typeString;
        if (adj != null){
            finalString += "\nThe game should be " + adj + ".";
        }
        finalString += "\n" + gameplayString;
        if (theme != null){
            finalString += "\nThe game should follow a(n) " + theme + " theme.";
        }
        
        EmbedBuilder builder = new EmbedBuilder();
    
        builder.setTitle("Idea \uD83D\uDCA1");
        builder.setDescription(finalString);
        //builder.setColor(new Color(0, 214, 255, 145));
        event.getChannel().sendMessageEmbeds(builder.build()).queue();
        
    }
}
