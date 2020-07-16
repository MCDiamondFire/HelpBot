package com.diamondfire.helpbot.components.dfranks;

import java.util.ArrayList;
import java.util.HashMap;

public enum Ranks {
    // Ranks
    NOBLE("Noble", 1, RankCategories.DONOR),
    EMPEROR("Emperor", 2, RankCategories.DONOR),
    MYTHIC("Mythic", 3, RankCategories.DONOR),
    OVERLORD("Overlord", 4, RankCategories.DONOR),
    // Support
    JRHELPER("JrHelper", 1, RankCategories.SUPPORT),
    HELPER("Helper", 2, RankCategories.SUPPORT),
    EXPERT("Expert", 3, RankCategories.SUPPORT),
    // Moderation
    JRMOD("JrMod", 1, RankCategories.MODERATION),
    MOD("Mod", 2, RankCategories.MODERATION),
    //Administration
    ADMIN("Admin", 3, RankCategories.MODERATION),
    OWNER("Owner", 4, RankCategories.MODERATION),
    //Retirement
    RETIRED("Retired", 1, RankCategories.RETIREMENT),
    EMERITUS("Emeritus", 2, RankCategories.RETIREMENT),
    //Special
    YOUTUBER("YT", 1, RankCategories.YOUTUBER),
    DEVELOPER("Dev", 1, RankCategories.DEVELOPER),
    BUILDER("B", 1, RankCategories.BUILDER);

    private static final HashMap<RankCategories, HashMap<Integer, Ranks>> RANK_LIST = new HashMap<>();

    static {
        for (Ranks tag : values()) {
            HashMap<Integer, Ranks> hash = RANK_LIST.get(tag.category);
            if (hash != null) {
                hash.put(tag.number, tag);
            } else {
                HashMap<Integer, Ranks> ranksHashMap = new HashMap<>();
                ranksHashMap.put(tag.number, tag);
                RANK_LIST.put(tag.category, ranksHashMap);
            }

        }
    }

    private String rankName;
    private int number;
    private RankCategories category;

    Ranks(String rankName, int number, RankCategories category) {
        this.rankName = rankName;
        this.number = number;
        this.category = category;
    }

    public static Ranks getRank(RankCategories category, int number) {
        return RANK_LIST.get(category).get(number);
    }

    public static Ranks[] getAllRanks(int donor, int support, int moderation, int retirement, int yt, int dev, int build) {
        ArrayList<Ranks> ranks = new ArrayList<>();
        ranks.add(getRank(RankCategories.DONOR, donor));
        ranks.add(getRank(RankCategories.SUPPORT, support));
        ranks.add(getRank(RankCategories.MODERATION, moderation));
        ranks.add(getRank(RankCategories.RETIREMENT, retirement));
        ranks.add(getRank(RankCategories.YOUTUBER, yt));
        ranks.add(getRank(RankCategories.DEVELOPER, dev));
        ranks.add(getRank(RankCategories.BUILDER, build));
        return ranks.toArray(new Ranks[0]);
    }

    public String getRankName() {
        return rankName;
    }

    public int getNumber() {
        return number;
    }
}
