package com.diamondfire.helpbot.df.ranks;

import com.diamondfire.helpbot.util.FakeEmote;

public enum Rank {
    // Ranks
    NOBLE("Noble", 1, RankBranch.DONOR, new FakeEmote("Noble",735940074285432834L)),
    EMPEROR("Emperor", 2, RankBranch.DONOR, new FakeEmote("Emperor", 735940074595680366L)),
    MYTHIC("Mythic", 3, RankBranch.DONOR, new FakeEmote("Mythic", 735940074662789130L)),
    OVERLORD("Overlord", 4, RankBranch.DONOR, new FakeEmote("Overlord", 735940074742612030L)),
    // Support
    JRHELPER("JrHelper", 1, RankBranch.SUPPORT, new FakeEmote("S_JrHelper", 759556039803666483L)),
    HELPER("Helper", 2, RankBranch.SUPPORT, new FakeEmote("Helper", 739900773026234431L)),
    EXPERT("Expert", 3, RankBranch.SUPPORT, new FakeEmote("S_Expert", 759556039733018668L)),
    // Moderation
    JRMOD("JrMod", 1, RankBranch.MODERATION, new FakeEmote("JrModerator", 739900772640096318L)),
    MOD("Mod", 2, RankBranch.MODERATION, new FakeEmote("Moderator", 739900772988223538L)),
    //Administration
    ADMIN("Admin", 3, RankBranch.MODERATION, new FakeEmote("Admin", 739900772963188856L)),
    OWNER("Owner", 4, RankBranch.MODERATION, new FakeEmote("Owner", 739900773030297631L)),
    //Retirement
    RETIRED("Retired", 1, RankBranch.RETIREMENT, new FakeEmote("Retired", 739900772719919195L)),
    EMERITUS("Emeritus", 2, RankBranch.RETIREMENT, new FakeEmote("Emeritus", 739900772648747132L)),
    //Special
    YOUTUBER("YT", 1, RankBranch.YOUTUBER, new FakeEmote("Youtube", 739900772954931241L)),
    DEVELOPER("Dev", 1, RankBranch.DEVELOPER, new FakeEmote("S_Developer", 759556041892823060L)),
    BUILDER("B", 1, RankBranch.BUILDER, new FakeEmote("Builder", 739900773017714789L));

    private final String rankName;
    private final int number;
    private final RankBranch category;
    private final FakeEmote emote;

    Rank(String rankName, int number, RankBranch category, FakeEmote emote) {
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

    public FakeEmote getRankEmote() {
        return emote;
    }
}

