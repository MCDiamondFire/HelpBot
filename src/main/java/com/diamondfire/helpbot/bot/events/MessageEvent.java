package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.df.filter.ChatFilters;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class MessageEvent extends ListenerAdapter {

    @Override
    public void onRawGateway(@NotNull RawGatewayEvent event) {
        if (event.getType().equals("INTERACTION_CREATE")) {

        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        if (ChatFilters.filterMessage(message)) {
            if (message.getContentDisplay().startsWith(HelpBotInstance.getConfig().getPrefix()) && !event.getAuthor().isBot()) {
                HelpBotInstance.getHandler().run(new CommandEvent(event.getJDA(), event.getResponseNumber(), message));
            }
        }
        
    }
    
}
