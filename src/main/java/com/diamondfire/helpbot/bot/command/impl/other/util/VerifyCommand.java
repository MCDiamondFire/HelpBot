package com.diamondfire.helpbot.bot.command.impl.other.util;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.ResultSet;

public class VerifyCommand extends Command {
    
    public static final long ROLE_ID = 349434193517740033L;
    
    @Override
    public String getName() {
        return "verify";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Verifies a player and inserts them into the verified database.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("user"),
                        new HelpContextArgument()
                                .name("mc name/uuid")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("user",
                        new DiscordUserArgument())
                .addArgument("mcname",
                        new StringArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        long user = event.getArgument("user");
        String minecraftPlayer = event.getArgument("mcname");
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, minecraftPlayer);
                    statement.setString(2, minecraftPlayer);
                }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        PresetBuilder preset = new PresetBuilder();
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                        event.reply(preset);
                        return;
                    }
                    
                    ResultSet set = result.getResult();
                    String uuid = set.getString("uuid");
                    String name = set.getString("name");
                    String userString = String.valueOf(user);
    
                    event.getGuild().retrieveMemberById(user).queue((member) -> {
                        builder.withPreset(
                                new InformativeReply(InformativeReplyType.SUCCESS, "Verified!")
                        );
                        Guild guild = event.getGuild();
                        guild.addRoleToMember(member, guild.getRoleById((VerifyCommand.ROLE_ID))).queue();
    
                        // Run the query before any messages sent to make sure that they are actually added.
                        new DatabaseQuery()
                                .query(new BasicQuery("UPDATE linked_accounts SET discord_id = null WHERE discord_id = ? ", (statement) -> {
                                    statement.setString(1, userString);
                                })).compile();
                        
                        new DatabaseQuery()
                                .query(new BasicQuery("INSERT INTO linked_accounts (player_uuid, player_name, discord_id) VALUES (?,?,?) ON DUPLICATE KEY UPDATE discord_id = ?", (statement) -> {
                                    statement.setString(1, uuid);
                                    statement.setString(2, name);
                                    statement.setString(3, userString);
                                    statement.setString(4, userString);
                                })).compile();
                        
                        event.reply(builder);
                        
                        Util.updateMember(member);
                        
                        PresetBuilder log = new PresetBuilder()
                                .withPreset(new InformativeReply(InformativeReplyType.SUCCESS, name + " was verified"));
                        Util.log(log);
                    }, (error) -> {
                        builder.withPreset(
                                new InformativeReply(InformativeReplyType.ERROR, "Discord user was not found!")
                        );
        
                        event.reply(builder);
                    });
                });
        
    }
}
