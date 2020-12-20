package com.diamondfire.helpbot.df.ranks;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.Emote;

public enum Rank {
    // Ranks
    NOBLE("Noble", 1, RankBranch.DONOR, "Noble"),
    EMPEROR("Emperor", 2, RankBranch.DONOR, "Emperor"),
    MYTHIC("Mythic", 3, RankBranch.DONOR, "Mythic"),
    OVERLORD("Overlord", 4, RankBranch.DONOR, "Overlord"),
    // Support
    JRHELPER("JrHelper", 1, RankBranch.SUPPORT, "S_JrHelper"),
    HELPER("Helper", 2, RankBranch.SUPPORT, "Helper"),
    EXPERT("Expert", 3, RankBranch.SUPPORT, "S_Expert"),
    // Moderation
    JRMOD("JrMod", 1, RankBranch.MODERATION, "JrModerator"),
    MOD("Mod", 2, RankBranch.MODERATION, "Moderator"),
    //Administration
    ADMIN("Admin", 3, RankBranch.MODERATION, "Admin"),
    OWNER("Owner", 4, RankBranch.MODERATION, "Owner"),
    //Retirement
    RETIRED("Retired", 1, RankBranch.RETIREMENT, "Retired"),
    EMERITUS("Emeritus", 2, RankBranch.RETIREMENT, "Emeritus"),
    //Special
    YOUTUBER("YT", 1, RankBranch.YOUTUBER, "Youtube"),
    DEVELOPER("Dev", 1, RankBranch.DEVELOPER, "S_Developer"),
    BUILDER("B", 1, RankBranch.BUILDER, "Builder");
    
    private final String rankName;
    private final int number;
    private final RankBranch category;
    private final Emote emote;
    
    Rank(String rankName, int number, RankBranch category, String emote) {
        this.rankName = rankName;
        this.number = number;
        this.category = category;
        this.emote = HelpBotInstance.getJda().getGuildById(615846886414483465L).getEmotesByName(emote, false).get(0);
    }
    
    public String getRankName() {
        return rankName;
    }
    
    public int getNumber() {
        return number;
    }
    
    public RankBranch getCategory() {
        return category;
    }
    
    public static Rank fromBranch(RankBranch branch, int rankNum) {
        for (Rank rank : Rank.values()) {
            if (rank.getNumber() == rankNum && rank.getCategory() == branch) {
                return rank;
            }
        }
        
        return null;
    }
    
    public Emote getRankEmote() {
        return emote;
    }
}

