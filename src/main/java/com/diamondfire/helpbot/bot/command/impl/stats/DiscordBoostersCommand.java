package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
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
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Current Discord Boosters", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        embed.setThumbnail("https://cdn.discordapp.com/emojis/699936318398136341.png?v=1");

        event.getGuild().findMembers((member -> member.getTimeBoosted() != null)).onSuccess((members) -> {
            embed.setDescription(StringUtil.listView(">", true, members.stream()
                    .sorted(Comparator.comparing(Member::getTimeBoosted))
                    .map(Member::getEffectiveName)
                    .toArray(String[]::new)));
            embed.setColor(new Color(255, 115, 250));
            event.reply(preset);
            guild.pruneMemberCache();
        });
    }

}


