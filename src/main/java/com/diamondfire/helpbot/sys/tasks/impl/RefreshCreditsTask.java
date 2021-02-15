package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.tasks.MidnightTask;

public class RefreshCreditsTask implements MidnightTask {
    
    @Override
    public void run() {
        PresetBuilder preset = new PresetBuilder();
        preset.withPreset(new InformativeReply(InformativeReplyType.INFO, "Credit log db has been updated"));
        RESPONSE_CHANNEL.sendMessage(preset.getEmbed().build()).queue();
        
        new DatabaseQuery()
                .query(new BasicQuery("INSERT INTO owen.creator_rankings_log(uuid, points, `rank`, date) " +
                        "SELECT uuid, points, cur_rank, CURRENT_DATE() FROM creator_rankings WHERE points > 4990;")).compile();
    }
}
