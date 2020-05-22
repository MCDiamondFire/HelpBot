package com.owen1212055.helpbot.events;

import com.owen1212055.helpbot.instance.BotInstance;
import com.owen1212055.helpbot.util.BotConstants;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MessageEvent extends ListenerAdapter {
    public MessageEvent() {
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        if (event.getMessage().getContentDisplay().startsWith(BotConstants.PREFIX)) {
            CommandEvent commandEvent = new CommandEvent(event.getJDA(), event.getResponseNumber(), event.getMessage());
            BotInstance.getHandler().run(commandEvent);

        }
    }

}
