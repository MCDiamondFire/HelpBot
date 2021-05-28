package com.diamondfire.helpbot.sys.interaction.button;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.util.TemporaryRunnableStorage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.Collections;
import java.util.function.Consumer;

public class ButtonHandler {
    
    private static final TemporaryRunnableStorage<Long, ButtonListener> BUTTON_CONSUMERS = new TemporaryRunnableStorage<>();
    
    public static void addListener(long user, Message message, Consumer<ButtonClickEvent> consumer, boolean persistent) {
        BUTTON_CONSUMERS.put(message.getIdLong(), new ButtonListener(user, consumer), () -> {
            HelpBotInstance.getJda()
                    .getTextChannelById(message.getChannel().getIdLong()).retrieveMessageById(message.getIdLong())
                    .flatMap((msg) -> persistent, (msg) -> message.editMessage(message).setActionRows(Collections.emptyList()))
                    .queue();
        }, persistent);
    }
    
    public static void addListener(long user, Message message, Consumer<ButtonClickEvent> consumer) {
        addListener(user, message, consumer, false);
    }
    
    public static void handleEvent(ButtonClickEvent event) {
        ButtonListener listener = BUTTON_CONSUMERS.get(event.getMessageIdLong());
        if (listener != null) {
            if (event.getUser().getIdLong() != listener.user) {
                event.deferReply(true).addEmbeds(
                        new PresetBuilder().withPreset(
                                new InformativeReply(InformativeReplyType.ERROR, "You can't use this button!"))
                                .getEmbed()
                                .build()
                ).queue();
                return;
            }
            
            BUTTON_CONSUMERS.expireKey(event.getMessageIdLong());
            listener.consumer.accept(event);
        }
    }
    
    private static class ButtonListener {
        
        private final long user;
        private final Consumer<ButtonClickEvent> consumer;
        
        private ButtonListener(long user, Consumer<ButtonClickEvent> consumer) {
            this.user = user;
            this.consumer = consumer;
        }
    }
    
}
