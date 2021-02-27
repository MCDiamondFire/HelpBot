package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.tasks.impl.MuteExpireTask;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.entities.*;

import java.time.*;
import java.util.Date;

public class ChannelMuteCommand extends Command {
    
    @Override
    public String getName() {
        return "channelmute";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Mutes a player from posing in a specific channel for one week (7 days)")
                .category(CommandCategory.OTHER)
                .addArgument(new HelpContextArgument().name("user"))
                .addArgument(new HelpContextArgument().name("channel"))
                .addArgument(new HelpContextArgument().name("duration"))
                .addArgument(new HelpContextArgument().name("reason").optional());
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        LocalDate nextWeek = LocalDate.now().plusDays(7);
        return new ArgumentSet()
                .addArgument("user", new DiscordUserArgument())
                .addArgument("channel", new StringArgument())
                .addArgument("duration", new SingleArgumentContainer<>(new TimeOffsetArgument()).optional(DateUtil.toDate(nextWeek)))
                .addArgument("reason", new StringArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        long user = event.getArgument("user");
        long channel = Long.parseLong(event.getArgument("channel"));
        Date duration = event.getArgument("duration");
        String reason = event.getArgument("reason");
        if (reason.length() == 0) {
            reason = "No reason given";
        }
        long timeLeft = duration.toInstant().minusSeconds(Instant.now().getEpochSecond()).toEpochMilli();
        event.getGuild().retrieveMemberById(user).queue((msg) -> {
                    //TODO Owen DBQuery
                    
                    
                    builder.withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS, "Muted!", String.format("User will be muted for ``%s``.", FormatUtil.formatMilliTime(timeLeft)))
                    );
                    Guild punishmentGuild = event.getGuild();
                    TextChannel textChannel = punishmentGuild.getTextChannelById(channel);
                    punishmentGuild.retrieveMemberById(user).queue((member) -> {
                        textChannel.putPermissionOverride(member).deny(net.dv8tion.jda.api.Permission.MESSAGE_ADD_REACTION, net.dv8tion.jda.api.Permission.MESSAGE_WRITE).queue();
                    });
                    
                    HelpBotInstance.getScheduler().schedule(new MuteExpireTask(user, duration, false));
                    event.reply(builder);
                }, (error) -> {
                    builder.withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Discord user was not found!")
                    );
                    event.reply(builder);
                }
        );
    }
}
