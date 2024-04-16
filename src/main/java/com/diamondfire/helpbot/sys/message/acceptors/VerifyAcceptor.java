package com.diamondfire.helpbot.sys.message.acceptors;

import com.diamondfire.helpbot.bot.command.impl.other.mod.VerifyCommand;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.sql.ResultSet;

public class VerifyAcceptor implements MessageAcceptor {
    
    @Override
    public boolean accept(Message message) {
        if (message.getChannel().getIdLong() != 422588582688063489L) {
            return false;
        }
        
        String contents = message.getContentRaw();
        int start = contents.indexOf('#');
        if (start == -1 || contents.length() < start + 6) {
            return false;
        }
        
        String key = contents.substring(start + 1, start + 6); // Remove #
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM hypercube.linked_accounts WHERE secret_key = ?", (statement) -> {
                    statement.setString(1, key);
                }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        message.addReaction(Emoji.fromUnicode("❌")).queue();
                    } else {
                        ResultSet set = result.getResult();
                        Member author = message.getMember();
                        
                        String uuid = set.getString("player_uuid");
                        String name = set.getString("player_name");
                        String userString = author.getId();
                        
                        Guild guild = message.getGuild();
                        guild.addRoleToMember(message.getMember(), guild.getRoleById((VerifyCommand.ROLE_ID))).queue();
                        
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
                        
                        Util.updateMember(author);
                        
                        PresetBuilder log = new PresetBuilder()
                                .withPreset(new InformativeReply(InformativeReplyType.SUCCESS, name + " was verified"));
                        Util.log(log);
                        message.delete().queue();
                    }
                });
        
        return false;
    }
}
