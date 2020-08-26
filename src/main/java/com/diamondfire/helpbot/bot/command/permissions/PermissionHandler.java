package com.diamondfire.helpbot.bot.command.permissions;

import net.dv8tion.jda.api.entities.Member;

import java.util.*;

public class PermissionHandler {

    private static final HashMap<Long, Permission> permsCache = new HashMap<>();

    public static Permission getPermission(Member member) {
        if (permsCache.containsKey(member.getIdLong())) {
            return permsCache.get(member.getIdLong());
        }
        // Return user if guild isn't df guild.
        if (member.getGuild().getIdLong() != 180793115223916544L) {
            return Permission.USER;
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
