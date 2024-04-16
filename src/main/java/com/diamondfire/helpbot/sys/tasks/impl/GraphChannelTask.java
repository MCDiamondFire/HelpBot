package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.graph.generators.*;
import com.diamondfire.helpbot.sys.graph.generators.context.TimeGraphContext;
import com.diamondfire.helpbot.sys.tasks.MidnightTask;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

public class GraphChannelTask implements MidnightTask {
    
    @Override
    public void run() {
        TextChannel channel = HelpBotInstance.getJda().getTextChannelById(736019542882517062L);
        channel.getHistoryFromBeginning(50).queue(messageHistory -> channel.purgeMessages(messageHistory.getRetrievedHistory()));
    
        TimeGraphContext context = new TimeGraphContext(TimeMode.DAILY, 14);
        channel.sendFiles(FileUpload.fromData(GraphGenerators.NEW_PLAYERS.createGraph(context))).queue();
        channel.sendFiles(FileUpload.fromData(GraphGenerators.UNIQUE_JOINS.createGraph(context))).queue();
    }
}
