package com.diamondfire.helpbot.sys.rolereact;

import java.util.*;

public enum ReactRole {
    UPDATES("\uD83D\uDCDC", 761778773711519747L),
    EVENTS("\uD83D\uDCC5", 761779264345079848L),
    STREAMS("\uD83D\uDDA5", 761779390161223681L),
    PROMOTIONS("\uD83D\uDC64", 761779521937735711L),
    SALES("\uD83C\uDFF7", 761779623322320916L);
    
    private static final Map<String, ReactRole> roleMap = new HashMap<>();
    private final String emoji;
    private final long roleID;
    
    static {
        for (ReactRole role : values()) {
            roleMap.put(role.getEmoji(), role);
        }
    }
    
    ReactRole(String emoji, long roleID) {
        this.emoji = emoji;
        this.roleID = roleID;
    }
    
    public static ReactRole fromEmoji(String emoji) {
        return roleMap.get(emoji);
    }
    
    public long getRoleID() {
        return roleID;
    }
    
    public String getEmoji() {
        return emoji;
    }
}
