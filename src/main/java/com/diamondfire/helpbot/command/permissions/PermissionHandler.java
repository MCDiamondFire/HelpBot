package com.diamondfire.helpbot.command.permissions;

import net.dv8tion.jda.api.entities.Member;

import java.util.Comparator;
import java.util.HashMap;

public class PermissionHandler {

    private static final HashMap<Long, Permission> permsCache = new HashMap<>();

    static {
        permsCache.put(246778942323818506L, Permission.BOT_DEVELOPER);
    }

    public static Permission getPermission(Member member) {
        if (permsCache.containsKey(member.getIdLong())) {
            return permsCache.get(member.getIdLong());
        }

        //Calculates the highest permission that the member has access to.
        Permission perm = member.getRoles().stream()
                .map((role) -> Permission.fromRole(role.getIdLong()))
                .max(Comparator.comparingInt(Permission::getPermissionLevel))
                .orElse(Permission.USER);


        permsCache.put(member.getIdLong(), perm);
        return perm;
    }
}
