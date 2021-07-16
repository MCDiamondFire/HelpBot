package com.diamondfire.helpbot.bot.command.impl.other.util;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.DiscordUserArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import net.dv8tion.jda.api.entities.*;

import java.sql.ResultSet;

public class UnmuteCommand extends Command {
    
    
    @Override
    public String getName() {
        return "unmute";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Unmutes a player and sends them a message containing the given reason.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("user"),
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
        String reason = event.getArgument("reason");
        
        event.getGuild().retrieveMemberById(user).queue((member) -> {
            new DatabaseQuery()
                    .query(new BasicQuery("SELECT * FROM owen.muted_members WHERE (muted_till > CURRENT_TIMESTAMP() && !handled) AND member = ? LIMIT 1", (state) -> {
                        state.setLong(1, member.getIdLong());
                    }))
                    .compile()
                    .run((result) -> {
                        if (result.isEmpty()) {
                            builder.withPreset(
                                    new InformativeReply(InformativeReplyType.ERROR, "Discord user is not muted!")
                            );
                            
                        } else {
                            Guild guild = event.getGuild();
                            ResultSet set = result.getResult();
                            boolean special = "Weekly Discussion Mute".equals(set.getString("reason"));
                            
                            if (special) {
                                TextChannel channel = guild.getTextChannelById(DiscussionMuteCommand.DISCUSSION_CHANNEL);
                                channel.getPermissionOverride(member).delete().queue();
                                
                            } else {
                                guild.removeRoleFromMember(member, guild.getRoleById(MuteCommand.ROLE_ID)).queue();
                            }
                            
                            new DatabaseQuery()
                                    .query(new BasicQuery("UPDATE owen.muted_members SET handled = true WHERE member = ?", (statement) -> statement.setLong(1, member.getIdLong())))
                                    .compile();
                            
                            member.getUser()
                                    .openPrivateChannel()
                                    .flatMap((e) -> e.sendMessage(String.format("Congratulations! Your mute has been removed early. \nReason: %s", reason)))
                                    .queue();
                            
                            builder.withPreset(
                                    new InformativeReply(InformativeReplyType.SUCCESS, "User has been unmuted and notified!")
                            );
                        }
                    });
            
            event.reply(builder);
        }, (error) -> {
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Discord user was not found!")
            );
            
            event.reply(builder);
        });
        
    }
}
