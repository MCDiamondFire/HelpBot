package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Comparator;

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
                .category(CommandCategory.STATS);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        Guild guild = event.getGuild();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Current Discord Boosters");
        builder.setThumbnail("https://cdn.discordapp.com/emojis/699936318398136341.png?v=1");

        event.getGuild().findMembers((member -> member.getTimeBoosted() != null)).onSuccess((members) -> {
            builder.setDescription(StringUtil.listView(members.stream()
                    .sorted(Comparator.comparing(Member::getTimeBoosted))
                    .map(Member::getEffectiveName)
                    .toArray(String[]::new), ">", true));
            builder.setColor(new Color(255, 115, 250));
            event.getChannel().sendMessage(builder.build()).queue();

            guild.pruneMemberCache();
        });
    }

}


