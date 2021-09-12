package com.diamondfire.helpbot.bot.command.impl.other.mod;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;

public class MutedCommand extends Command {
    
    @Override
    public String getName() {
        return "muted";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Provides a list of all muted discord members.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Rank getRank() {
        return Rank.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Currently Muted Members", null)
                );
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM owen.muted_members WHERE (muted_till > CURRENT_TIMESTAMP() && !handled) ORDER BY muted_till;"))
                .compile()
                .run((result) -> {
                    // Select unique names.
                    List<String> names = new ArrayList<>();
                    
                    List<String> muted = new ArrayList<>();
                    for (ResultSet set : result) {
                        long duration = set.getTimestamp("muted_till").toInstant().minusMillis(Instant.now().toEpochMilli()).toEpochMilli();
                        String reason = set.getString("reason");
                        String muted_by = set.getString("muted_by");
                        String name = set.getString("member");
                        
                        if (names.contains(name)) {
                            continue;
                        } else {
                            names.add(name);
                        }
                        
                        muted.add("<@" + name + ">\n" + String.format("For: `%s`\nReason: %s (<@%s>)", FormatUtil.formatMilliTime(duration), reason, muted_by));
                    }
                    
                    EmbedUtil.addFields(builder.getEmbed(), muted, "", false);
                    event.reply(builder);
                });
        
    }
}
