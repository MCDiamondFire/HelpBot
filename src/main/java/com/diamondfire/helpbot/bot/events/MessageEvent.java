package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MessageEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        if (message.getContentDisplay().startsWith(HelpBotInstance.getConfig().getPrefix()) && !event.getAuthor().isBot()) {
            HelpBotInstance.getHandler().run(new CommandEvent(event.getJDA(), event.getResponseNumber(), message));
        }

    }

}
