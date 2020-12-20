package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.stats.*;
import com.diamondfire.helpbot.sys.tasks.MidnightTask;
import net.dv8tion.jda.api.entities.TextChannel;

public class GraphChannelTask implements MidnightTask {
    
    @Override
    public void run() {
        TextChannel channel = HelpBotInstance.getJda().getTextChannelById(736019542882517062L);
        channel.getHistoryFromBeginning(50).queue(messageHistory -> channel.purgeMessages(messageHistory.getRetrievedHistory()));
        
        NewJoinGraphCommand.generateGraph("daily", 14, channel);
        PlayerJoinGraphCommand.generateGraph("daily", 14, channel);
    }
}
