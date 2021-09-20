package com.diamondfire.helpbot.bot.command.permissions;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.Member;

import java.util.Comparator;

public class PermissionHandler {
    
    
    public static Permission getPermission(Member member) {
        // Return user if guild isn't df guild.
        if (member.getGuild().getIdLong() != HelpBotInstance.DF_GUILD) {
            return Permission.USER;
        }
        
        //Calculates the highest permission that the member has access to.
        Permission perm = member.getRoles().stream()
                .map((role) -> Permission.fromRole(role.getIdLong()))
                .max(Comparator.comparingInt(Permission::getPermissionLevel))
                .orElse(Permission.USER);
        
        return perm;
    }
    
}
