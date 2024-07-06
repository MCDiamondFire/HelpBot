package com.diamondfire.helpbot.df.ranks;

import java.util.*;

public enum RankBranch {
    DONOR,
    VIP,
    RETIREMENT,
    BUILDER,
    YOUTUBER,
    SUPPORT,
    MODERATION,
    ADMINISTRATION;
    
    public static final Map<String, RankBranch> databaseMap = new HashMap<>();
    
    static {
        for (RankBranch branch : values()) {
            databaseMap.put(branch.toString(), branch);
        }
    }
    
    
}
