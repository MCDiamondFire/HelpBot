package com.diamondfire.helpbot.bot.command.permissions;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.Member;

import java.util.Comparator;

public class RankHandler {
    
    
    public static Rank getPermission(Member member) {
        // Return user if guild isn't df guild.
        if (member.getGuild().getIdLong() != HelpBotInstance.DF_GUILD) {
            return Rank.USER;
        }
        
        //Calculates the highest permission that the member has access to.
        Rank perm = member.getRoles().stream()
                .map((role) -> Rank.fromRole(role.getIdLong()))
                .max(Comparator.comparingInt(Rank::getPermissionLevel))
                .orElse(Rank.USER);
        
        return perm;
    }
    
}
