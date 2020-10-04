package com.diamondfire.helpbot.sys.tasks;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.TextChannel;

public interface LoopingTask extends Runnable {
    TextChannel RESPONSE_CHANNEL = HelpBotInstance.getJda().getTextChannelById(762447660745359361L);

    long getInitialStart();

    long getNextLoop();

}
