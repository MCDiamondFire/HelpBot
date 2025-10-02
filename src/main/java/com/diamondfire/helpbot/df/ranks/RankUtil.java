package com.diamondfire.helpbot.df.ranks;

import java.sql.*;
import java.util.*;

public class RankUtil {
    
    public static Rank[] getRanks(ResultSet set) {
        List<Rank> ranks = new ArrayList<>();
        try {
            ResultSetMetaData metaData = set.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                RankBranch branch = RankBranch.databaseMap.get(metaData.getColumnName(i).toUpperCase());
                if (branch == null) {
                    continue;
                }
                int num = set.getInt(i);
                
                Rank rank = Rank.fromBranch(branch, num);
                if (rank != null) {
                    ranks.add(rank);
                }
                
            }
            
        } catch (SQLException ignored) {
        }
        
        return ranks.stream().sorted(Comparator.comparingInt(Enum::ordinal)).toArray(Rank[]::new);
    }
    
    public static Rank getHighRank(ResultSet set) {
        return getHighRank(getRanks(set));
    }
    
    public static Rank getHighRank(Rank[] ranks) {
        if (ranks.length == 0) {
            return null;
        }
        
        return ranks[ranks.length - 1];
    }
}
