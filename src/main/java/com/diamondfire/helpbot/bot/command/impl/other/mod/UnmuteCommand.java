package com.diamondfire.helpbot.bot.command.impl.other.mod;

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
        
        event.getGuild().retrieveMemberById(user).submit().thenAccept((member) -> {
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
                            event.reply(builder);
                        } else {
                            Guild guild = event.getGuild();
                            Role mutedRole = guild.getRoleById(MuteCommand.ROLE_ID);
                            if (mutedRole == null) {
                                event.reply(new PresetBuilder().withPreset(new InformativeReply(
                                        InformativeReplyType.ERROR, "Could not find the muted role!")));
                                return;
                            }
                            guild.removeRoleFromMember(member, mutedRole).submit().thenRun(() -> {
                                new DatabaseQuery()
                                        .query(new BasicQuery("UPDATE owen.muted_members SET handled = true WHERE member = ?", (statement) -> statement.setLong(1, member.getIdLong())))
                                        .compile();

                                member.getUser()
                                        .openPrivateChannel()
                                        .flatMap((e) -> e.sendMessage(String.format("Congratulations! Your mute has been removed early. \nReason: %s", reason)))
                                        .queue(sent -> event.reply(new PresetBuilder().withPreset(new InformativeReply(
                                                InformativeReplyType.SUCCESS, "User has been unmuted and notified!"))),
                                                error -> event.reply(new PresetBuilder().withPreset(new InformativeReply(
                                                        InformativeReplyType.SUCCESS, "User has been unmuted, but I could not send them a private message."))));
                            }).exceptionally(error -> {
                                replyFailure(event, error);
                                return null;
                            });
                        }
                    });
            
        }).exceptionally((error) -> {
            replyFailure(event, error);
            return null;
        });
        
    }

    private static void replyFailure(CommandEvent event, Throwable error) {
        error.printStackTrace();
        event.reply(new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR,
                "Could not complete the unmute. Please check the user, my permissions, and try again.")));
    }
}
