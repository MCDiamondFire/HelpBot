package com.diamondfire.helpbot.df.ranks;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.Emote;

public enum Rank {
    // Ranks
    NOBLE("Noble", 1, RankBranch.DONOR, "<:dfr_noble:964324460624740382>"),
    EMPEROR("Emperor", 2, RankBranch.DONOR, "<:dfr_emperor:964324460666699776>"),
    MYTHIC("Mythic", 3, RankBranch.DONOR, "<:dfr_mythic:964324460767354880>"),
    OVERLORD("Overlord", 4, RankBranch.DONOR, "<:dfr_overlord:964324460595384370>"),
    //Retirement
    RETIRED("Retired", 1, RankBranch.RETIREMENT, "<:dfr_retired:964324460641521795>"),
    EMERITUS("Emeritus", 2, RankBranch.RETIREMENT, "<:dfr_emeritus:964324460633141249>"),
    //Special
    BUILDER("Builder", 1, RankBranch.BUILDER, "<:dfr_builder:964324460222111755>"),
    YOUTUBER("YouTuber", 1, RankBranch.YOUTUBER, "<:dfr_youtuber:964324460628967424>"),
    // Support
    JRHELPER("JrHelper", 1, RankBranch.SUPPORT, "<:dfr_jrhelper:964324460272451655>"),
    HELPER("Helper", 2, RankBranch.SUPPORT, "<:dfr_helper:964324460628951122>"),
    SR_HELPER("SrHelper", 3, RankBranch.SUPPORT, "<:dfr_srhelper:964324460628942919>"),
    // Moderation
    JRMOD("JrMod", 1, RankBranch.MODERATION, "<:dfr_jrmod:964324460616364083>"),
    MOD("Mod", 2, RankBranch.MODERATION, "<:dfr_mod:964324460603785246>"),
    SR_MOD("SrMod", 3, RankBranch.MODERATION, "<:dfr_srmod:964324460570214441>"),
    //Administration
    DEVELOPER("Dev", 1, RankBranch.ADMINISTRATION, "<:dfr_dev:964324460624760862>"),
    ADMIN("Admin", 2, RankBranch.ADMINISTRATION, "<:dfr_admin:964324460628942939>"),
    OWNER("Owner", 3, RankBranch.ADMINISTRATION, "<:dfr_owner:964324460670890034>");
    
    private final String rankName;
    private final int number;
    private final RankBranch category;
    private final String emote;
    
    Rank(String rankName, int number, RankBranch category, String emote) {
        this.rankName = rankName;
        this.number = number;
        this.category = category;
        this.emote = emote;
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
    
    public String getRankEmote() {
        return emote;
    }
}

