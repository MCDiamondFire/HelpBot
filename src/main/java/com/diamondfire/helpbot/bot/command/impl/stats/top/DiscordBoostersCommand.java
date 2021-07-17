package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DiscordBoostersCommand extends Command {
    
    @Override
    public String getName() {
        return "discordboosters";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"discordb", "dboosts"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current members who are boosting the discord server.")
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Guild guild = event.getGuild();
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Discord Server Boosters", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        embed.setThumbnail("https://cdn.discordapp.com/emojis/699936318398136341.png?v=1");
        
        event.getGuild().findMembers((member -> member.getTimeBoosted() != null)).onSuccess((members) -> {
            members.sort(Comparator.comparing(Member::getTimeBoosted));
            embed.setDescription("A list of members that are currently boosting this Discord server.\n\n"+
                    getFormattedBoosters(members));
            embed.setColor(new Color(255, 115, 250));
            event.reply(preset);
            guild.pruneMemberCache();
        });
    }
    
    private static String getFormattedBoosters(List<Member> members) {
        if (members.size() == 0) return "*None*";
        
        else {
            SimpleDateFormat format = new SimpleDateFormat("y'y'M'm'D'd'");
            
            List<String> elements = new ArrayList<>();
            for (Member member : members) {
                String timeBoosted = format.format(member.getTimeBoosted());
                elements.add(member.getAsMention() + " - " + timeBoosted);
            }
            
            return String.join("\n", elements);
        }
    }
    
}


