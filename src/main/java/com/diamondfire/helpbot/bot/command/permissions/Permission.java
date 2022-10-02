package com.diamondfire.helpbot.bot.command.permissions;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.Command;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;
import java.util.stream.Collectors;

public enum Permission {
    BOT_DEVELOPER(999),
    DEVELOPER(999),
    // Ask DragonSlasher, not me.
    ADMINISTRATOR(666),
    MODERATION(5),
    SR_HELPER(4),
    SUPPORT(3),
    RETIRED_SUPPORT(2),
    USER(1);
    
    private static final HashMap<Long, Permission> roleMap = new HashMap<>();
    private static final HashMap<Command, Set<Long>> overrides = new HashMap<>();
    
    static {
        for (Permission perm : values()) {
            roleMap.put(perm.getRole(), perm);
        }
    }
    
    private final long role;
    private final int permissionLevel;
    
    Permission(int permissionLevel) {
        this.role = HelpBotInstance.getConfig().getPermissionRoleMap().get(this.name());
        this.permissionLevel = permissionLevel;
    }
    
    public static Permission fromRole(long roleID) {
        Permission perm = roleMap.get(roleID);
        if (perm == null) {
            return USER;
        }
        return perm;
    }
    
    public Permission setOverrides(Command command, Long... userIds) {
        overrides.put(command, Arrays.stream(userIds).collect(Collectors.toSet()));
        return this;
    }
    
    public static Set<Long> getOverrides(Command command) {
        return Objects.requireNonNullElse(overrides.get(command), new HashSet<>());
    }
    
    public long getRole() {
        return role;
    }
    
    public int getPermissionLevel() {
        return permissionLevel;
    }
    
    public boolean hasPermission(Member member) {
        return hasPermission(PermissionHandler.getPermission(member));
    }
    
    public boolean hasPermission(Permission permission) {
        return getPermissionLevel() <= permission.getPermissionLevel();
    }
}
