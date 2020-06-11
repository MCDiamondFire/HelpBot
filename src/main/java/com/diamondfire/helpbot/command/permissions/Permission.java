package com.diamondfire.helpbot.command.permissions;

import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;

public enum Permission {
    BOT_DEVELOPER(589238520145510400L, 999),
    ADMINISTRATOR(180794313494495233L, 3),
    EXPERT(299109861696995329L, 3),
    SUPPORT(180794530398732288L, 2),
    USER(349434193517740033L, 1);

    private static final HashMap<Long, Permission> roleMap = new HashMap<>();

    static {
        for (Permission perm : values()) {
            roleMap.put(perm.getRole(), perm);
        }
    }

    private final long role;
    private final int permissionLevel;

    Permission(long roleID, int permissionLevel) {
        this.role = roleID;
        this.permissionLevel = permissionLevel;
    }

    public static Permission fromRole(long roleID) {
        Permission perm = roleMap.get(roleID);
        if (perm == null) {
            return USER;
        }
        return perm;
    }

    public long getRole() {
        return role;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public boolean hasPermission(Member member) {
        return getPermissionLevel() <= PermissionHandler.getPermission(member).getPermissionLevel();
    }
}
