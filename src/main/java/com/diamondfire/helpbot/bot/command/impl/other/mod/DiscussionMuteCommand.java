package com.diamondfire.helpbot.bot.command.impl.other.mod;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.tasks.impl.MuteExpireTask;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.entities.*;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DiscussionMuteCommand extends Command {
    
    public static final long DISCUSSION_CHANNEL = HelpBotInstance.getConfig().getDiscussionChannel();
    
    @Override
    public String getName() {
        return "discussionmute";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"weeklymute", "weekmute", "discmute"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Mutes a player from discussion till the next Monday 6PM EST.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("user"),
                        new HelpContextArgument()
                                .name("duration")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        
        return new ArgumentSet()
                .addArgument("user",
                        new DiscordUserArgument())
                .addArgument("duration",
                        new SingleArgumentContainer<>(new TimeOffsetArgument()).optional(event -> DateUtil.toDate(nextMonday)));
    }
    
    @Override
    public Rank getRank() {
        return Rank.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        long user = event.getArgument("user");
        Date duration = event.getArgument("duration");
        long timeLeft = duration.toInstant().minusSeconds(Instant.now().getEpochSecond()).toEpochMilli();
        
        event.getGuild().retrieveMemberById(user).queue((msg) -> {
            new DatabaseQuery()
                    .query(new BasicQuery("INSERT INTO owen.muted_members (member,muted_by,muted_at,muted_till,reason) VALUES (?,?,CURRENT_TIMESTAMP(),?,'Weekly Discussion Mute')", (statement) -> {
                        statement.setLong(1, user);
                        statement.setLong(2, event.getAuthor().getIdLong());
                        statement.setTimestamp(3, DateUtil.toTimeStamp(duration));
                        
                    }))
                    .compile();
            
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.SUCCESS, "Muted!", String.format("Player will be muted for ``%s``.", FormatUtil.formatMilliTime(timeLeft)))
            );
            Guild guild = event.getGuild();
            TextChannel channel = guild.getTextChannelById(DISCUSSION_CHANNEL);
            guild.retrieveMemberById(user).queue((member) -> {
                channel.putPermissionOverride(member).deny(net.dv8tion.jda.api.Permission.MESSAGE_ADD_REACTION, net.dv8tion.jda.api.Permission.MESSAGE_WRITE).queue();
            });
            
            HelpBotInstance.getScheduler().schedule(new MuteExpireTask(user, duration, true));
            event.reply(builder);
        }, (error) -> {
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Discord user was not found!")
            );
            
            event.reply(builder);
        });
        
    }
}
