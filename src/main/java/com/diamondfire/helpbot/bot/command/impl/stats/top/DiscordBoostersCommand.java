package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.time.Instant;
import java.util.List;
import java.util.*;

public class DiscordBoostersCommand extends Command {
    private static final int PAGE_SIZE = 20;
    private static final String BOOST_ICON = "https://cdn.discordapp.com/emojis/699936318398136341.png";
    
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
                .description("Lists current Discord server boosters, starting with the longest-running boosts.")
                .category(CommandCategory.GENERAL_STATS)
                .addArgument(new HelpContextArgument().name("page").optional());
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("page", new SingleArgumentContainer<>(new ClampedIntegerArgument(1)).optional(1));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Guild guild = event.getGuild();
        int page = event.getArgument("page");
        guild.findMembers(member -> member.getTimeBoosted() != null)
                .onSuccess(members -> {
                    try {
                        List<Booster> boosters = new ArrayList<>();
                        for (Member member : members) {
                            var boosted = member.getTimeBoosted();
                            if (boosted != null) {
                                boosters.add(new Booster(member.getIdLong(), boosted.toInstant()));
                            }
                        }
                        event.reply(createMessage(boosters, guild.getBoostCount(), page));
                    } catch (Exception error) {
                        replyFailure(event, error);
                    } finally {
                        guild.pruneMemberCache();
                    }
                })
                .onError(error -> {
                    guild.pruneMemberCache();
                    replyFailure(event, error);
                });
    }

    static MessageCreateData createMessage(List<Booster> boosters, int boostCount, int requestedPage) {
        List<Booster> sorted = boosters.stream()
                .sorted(Comparator.comparing(Booster::since).thenComparingLong(Booster::userId))
                .toList();
        int pages = Math.max(1, (sorted.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.max(1, Math.min(requestedPage, pages));
        String summary = "**" + sorted.size() + (sorted.size() == 1 ? " member boosting" : " members boosting") +
                "** · **" + boostCount + (boostCount == 1 ? " server boost" : " server boosts") + "**";

        List<ContainerChildComponent> components = new ArrayList<>();
        components.add(Section.of(Thumbnail.fromUrl(BOOST_ICON).withDescription("Server Boost"),
                TextDisplay.of("## Discord Server Boosters\n" + summary + "\nThank you for supporting the server!")));
        components.add(Separator.createDivider(Separator.Spacing.SMALL));
        if (sorted.isEmpty()) {
            components.add(TextDisplay.of("No members are boosting the server right now."));
        } else {
            int start = (page - 1) * PAGE_SIZE;
            List<String> entries = new ArrayList<>();
            for (Booster booster : sorted.subList(start, Math.min(start + PAGE_SIZE, sorted.size()))) {
                entries.add("<@" + booster.userId() + ">\nBoosting since " +
                        TimeFormat.DATE_LONG.format(booster.since()) + " · " + TimeFormat.RELATIVE.format(booster.since()));
            }
            components.add(TextDisplay.of(String.join("\n\n", entries)));
        }
        String footer = "-# Page " + page + " of " + pages + " · Longest-running boosts first";
        if (pages > 1) {
            footer += "\n-# Use the optional page argument to see more boosters.";
        }
        components.add(TextDisplay.of(footer));
        return new MessageCreateBuilder().useComponentsV2()
                .setComponents(Container.of(components).withAccentColor(0xFF73FA))
                .setAllowedMentions(Collections.emptySet())
                .build();
    }

    private static void replyFailure(CommandEvent event, Throwable error) {
        error.printStackTrace();
        event.reply(new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR,
                "Could not load server boosters. Please try again.")));
    }

    record Booster(long userId, Instant since) {}
}


