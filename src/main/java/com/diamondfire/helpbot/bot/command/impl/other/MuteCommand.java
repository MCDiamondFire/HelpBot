package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
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
import net.dv8tion.jda.api.entities.Guild;

import java.time.Instant;
import java.util.Date;

public class MuteCommand extends Command {
    
    public static final long ROLE_ID = 583693615159705600L;
    
    @Override
    public String getName() {
        return "mute";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Mutes a player for a certain duration. The mute will automatically expire when the duration is over.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("user"),
                        new HelpContextArgument()
                                .name("duration"),
                        new HelpContextArgument()
                                .name("reason")
                                .optional()
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("user",
                        new DiscordUserArgument())
                .addArgument("duration",
                        new TimeOffsetArgument())
                .addArgument("reason",
                        new SingleArgumentContainer<>(new MessageArgument()).optional("Not Specified"));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        long user = event.getArgument("user");
        Date duration = event.getArgument("duration");
        long timeLeft = duration.toInstant().minusSeconds(Instant.now().getEpochSecond()).toEpochMilli();
        
        event.getGuild().retrieveMemberById(user).queue((member) -> {
            new DatabaseQuery()
                    .query(new BasicQuery("INSERT INTO owen.muted_members (member,muted_by,muted_at,muted_till,reason) VALUES (?,?,CURRENT_TIMESTAMP(),?,?)", (statement) -> {
                        statement.setLong(1, user);
                        statement.setLong(2, event.getAuthor().getIdLong());
                        statement.setTimestamp(3, DateUtil.toTimeStamp(duration));
                        statement.setString(4, event.getArgument("reason"));
                        
                    }))
                    .compile();
            
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.SUCCESS, "Muted!", String.format("Player will be muted for ``%s``. They have been notified.", FormatUtil.formatMilliTime(timeLeft)))
            );
            Guild guild = event.getGuild();
            guild.addRoleToMember(member, guild.getRoleById((MuteCommand.ROLE_ID))).queue();
            member.getUser()
                    .openPrivateChannel()
                    .flatMap((e) -> e.sendMessage(String.format("You have been muted for %s for %s", FormatUtil.formatMilliTime(timeLeft), event.getArgument("reason"))))
                    .queue();
            HelpBotInstance.getScheduler().schedule(new MuteExpireTask(user, duration));
            event.reply(builder);
        }, (error) -> {
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Discord user was not found!")
            );
            
            event.reply(builder);
        });
        
    }
}
