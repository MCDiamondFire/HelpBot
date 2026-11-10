package com.diamondfire.helpbot.sys.tag;

import java.util.List;

/**
 * Tags that are inserted when the tags table is first created.
 */
final class DefaultTags {
    
    static final List<Tag> TAGS = List.of(
            new Tag("limits", "Plot Limits",
                    "» Guides and Tutorials Collection\nhttps://mcdiamondfire.com/threads/guides-collection.37/",
                    808966728201666620L, "https://cdn.discordapp.com/attachments/705205549498892299/1251932252833841203/image.png?ex=66706044&is=666f0ec4&hm=612053ed43d2d18f60c3b158b1cd484eac9570a68be126e5fbbae490409f636b&"),
            new Tag("funcvsproc", "Function vs Process",
                    "In simple terms, Functions will not continue the code it was called from until it is finished. Processes will do the opposite and create a new 'thread'. Below is a diagram to help demonstrate this.\n\nFor more information about functions, [click here](https://github.com/RyanLandDev/diamondfire-wiki/blob/master/docs/Code_Blocks/Functions.md).\nFor more information about processes, [click here](https://github.com/RyanLandDev/diamondfire-wiki/blob/master/docs/Code_Blocks/Processes.md).\n\n*Diagram made by S4nders*",
                    808966728201666620L, "https://cdn.discordapp.com/attachments/836724437528608798/836724787002343474/unknown.png"),
            new Tag("actionall", "How do I do this to all players/entities?",
                    "You can *shift-rightclick* any **Player or Entity action** to select the target this action will happen to. From here, you can click All Players to make the action apply to all players.\n\nAs an example, you can use **Player Action: Send Message** with the target All Players to send this message to all players.",
                    808966728201666620L, ""),
            new Tag("samman", "SamMan\\_",
                    "Mr. SamMan\\_\nSam me a man\nMake him the Sammest\nMan Sam handyman\nGive him a Sam\nA man underscore\nTell him that his manly Sams are Sammer\nSamMan\\_\nI'm not a Sam\nI don't have\nA Sam handyman\nPlease Mr. handyman Sam\nMr. SamMan\\_ Sam me a man\n\nMr. SamMan\\_\nSam me a man\nMake him the Sammest\nMan Sam handyman\nGive him a Sam\nA man underscore\nTell him that his manly Sams are Sammer\nSamMan\\_\nI'm not a Sam\nI don't have\nA Sam handyman\nPlease Mr. handyman Sam\nMr. SamMan\\_ Sam me a man\n\nMr. SamMan\\_\nSam me a man\nMake him the Sammest\nMan Sam handyman\nGive him a Sam\nA man underscore\nTell him that his manly Sams are Sammer\nSamMan\\_\nI'm not a Sam\nI don't have\nA Sam handyman\nPlease Mr. handyman Sam\nMr. SamMan\\_ please\nPlease\nPlease\nMr SamMan\\_ Sam me a man",
                    334011618566340608L, ""),
            new Tag("textcodes", "Text Codes",
                    "Text codes are very useful! View a guide by [clicking here](https://github.com/RyanLandDev/diamondfire-wiki/blob/master/docs/Other/Code_Related/Text_Codes.md#text-codes).",
                    808966728201666620L, ""),
            new Tag("contribute", "Contributing to HelpBot",
                    "If you'd like to contribute to HelpBot, please check out its [GitHub repository](https://github.com/MCDiamondFire/HelpBot)! You can contribute using a pull request.",
                    808966728201666620L, ""),
            new Tag("recode", "recode",
                    "recode was a community-made utility mod with various QOL features to make\ndevelopment easier and improve your DiamondFire experience.\n\n**This mod is no longer being maintained**, check out ?codeclient instead.",
                    397045621741322240L, ""),
            new Tag("codeclient", "CodeClient",
                    "CodeClient is a community-made utility mod with various QOL features to make development easier and improve your DiamondFire experience.\n\nYou can download and view more information about it on it's modrinth page:\nhttps://modrinth.com/mod/codeclient",
                    397045621741322240L, ""),
            new Tag("spark-interest", "please",
                    "please forward spark questions to <#1318773342286643210>",
                    916875577015799818L, ""),
            new Tag("mace", "Mace Roulette",
                    "A minigame made by user \"flopsuh\" that he has promoted on TikTok and Youtube Shorts, garnering large amounts of popularity.",
                    1246213314766180413L, "")
    );
    
    private DefaultTags() {
    }
}
