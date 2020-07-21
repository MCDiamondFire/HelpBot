package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;

public class MessageEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getContentDisplay().startsWith(HelpBotInstance.getConfig().getPrefix()) && !event.getAuthor().isBot()) {
            try {
                CommandEvent commandEvent = new CommandEvent(event.getJDA(), event.getResponseNumber(), message);
                HelpBotInstance.getHandler().run(commandEvent);
            } catch (IllegalArgumentException e) {
                PresetBuilder preset = new PresetBuilder()
                        .withPreset(
                                new InformativeReply(InformativeReplyType.ERROR, "Invalid Argument", e.getMessage())
                        );

                message.getChannel().sendMessage(preset.getEmbed().build()).queue();
            }

        }

    }

}
