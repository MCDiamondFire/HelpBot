package com.diamondfire.helpbot.bot.command.permissions;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.Command;
import net.dv8tion.jda.api.entities.*;

import java.util.*;
import java.util.stream.Collectors;

public enum Rank {
    BOT_DEVELOPER(589238520145510400L, 999),
    DEVELOPER(519740942861860874L, 999),
    // Ask DragonSlasher, not me.
    ADMINISTRATOR(180794313494495233L, 666),
    MODERATION(180794061429538816L, 5),
    EXPERT(299109861696995329L, 4),
    SUPPORT(180794530398732288L, 3),
    RETIRED_SUPPORT(235159617108181003L, 2),
    USER(349434193517740033L, 1);
    
    private static final HashMap<Long, Rank> roleMap = new HashMap<>();
    private static final HashMap<Command, Set<Long>> overrides = new HashMap<>();
    
    static {
        for (Rank perm : values()) {
            roleMap.put(perm.getRoleId(), perm);
        }
    }
    
    private final long role;
    private final int permissionLevel;
    
    Rank(long roleID, int permissionLevel) {
        this.role = roleID;
        this.permissionLevel = permissionLevel;
    }
    
    public static Rank fromRole(long roleID) {
        Rank perm = roleMap.get(roleID);
        if (perm == null) {
            return USER;
        }
        return perm;
    }
    
    public Rank setOverrides(Command command, Long... userIds) {
        overrides.put(command, Arrays.stream(userIds).collect(Collectors.toSet()));
        return this;
    }
    
    public static Set<Long> getOverrides(Command command) {
        return Objects.requireNonNullElse(overrides.get(command), new HashSet<>());
    }
    
    public Role getRole() {
        return HelpBotInstance.getJda().getRoleById(getRoleId());
    }
    
    public long getRoleId() {
        return role;
    }
    
    public int getPermissionLevel() {
        return permissionLevel;
    }
    
    public boolean hasPermission(Member member) {
        return hasPermission(RankHandler.getPermission(member));
    }
    
    public boolean hasPermission(Rank permission) {
        return getPermissionLevel() <= permission.getPermissionLevel();
    }
}
