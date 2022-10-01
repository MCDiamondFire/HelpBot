package com.diamondfire.helpbot.bot.command.impl.other.mod;

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
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
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
                .description("Mutes a player from posting in a specific channel for a specified duration.")
                .category(CommandCategory.OTHER)
                .addArgument(new HelpContextArgument().name("user"))
                .addArgument(new HelpContextArgument().name("channel"))
                .addArgument(new HelpContextArgument().name("duration"))
                .addArgument(new HelpContextArgument().name("reason").optional());
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("user", new DiscordUserArgument())
                .addArgument("channel", new LongArgument())
                .addArgument("duration", new SingleArgumentContainer<>(new TimeOffsetArgument()).optional(null))
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
        long channel = event.getArgument("channel");
        Date duration = event.getArgument("duration");
        if (duration == null) {
            duration = DateUtil.toDate(LocalDate.now().plusDays(7));
        }
        String reason = event.getArgument("reason");
        if (reason.length() == 0) {
            reason = "No reason given.";
        }
        long timeLeft = duration.toInstant().minusSeconds(Instant.now().getEpochSecond()).toEpochMilli();
        Date finalDuration = duration;
        event.getGuild().retrieveMemberById(user).queue((msg) -> {
                    event.getGuild().retrieveMemberById(user).queue((member) -> {
                        new DatabaseQuery()
                                .query(new BasicQuery("INSERT INTO owen.muted_members (member,muted_by,muted_at,muted_till,reason) VALUES (?,?,CURRENT_TIMESTAMP(),?,?)", (statement) -> {
                                    statement.setLong(1, user);
                                    statement.setLong(2, event.getAuthor().getIdLong());
                                    statement.setTimestamp(3, DateUtil.toTimeStamp(finalDuration));
                                    statement.setString(4, event.getArgument("reason"));
                                    
                                }))
                                .compile();
                    });
                    
                    builder.withPreset(
                            new InformativeReply(InformativeReplyType.SUCCESS, "Muted!",
                                    String.format("User will be muted in <#%s> for `%s`.", channel, FormatUtil.formatMilliTime(timeLeft)))
                    );
                    Guild punishmentGuild = event.getGuild();
                    TextChannel textChannel = punishmentGuild.getTextChannelById(channel);
                    punishmentGuild.retrieveMemberById(user).queue((member) -> {
                        textChannel.putPermissionOverride(member).deny(net.dv8tion.jda.api.Permission.MESSAGE_ADD_REACTION, net.dv8tion.jda.api.Permission.MESSAGE_WRITE).queue();
                    });
                    
                    HelpBotInstance.getScheduler().schedule(new MuteExpireTask(user, finalDuration));
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
