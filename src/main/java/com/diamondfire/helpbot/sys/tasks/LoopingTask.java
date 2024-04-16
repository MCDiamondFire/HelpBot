package com.diamondfire.helpbot.sys.tasks;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public interface LoopingTask extends Runnable {
    
    TextChannel RESPONSE_CHANNEL = HelpBotInstance.getJda().getTextChannelById(HelpBotInstance.LOG_CHANNEL);
    
    long getInitialStart();
    
    long getNextLoop();
    
}
