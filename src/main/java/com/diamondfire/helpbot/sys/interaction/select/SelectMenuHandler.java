package com.diamondfire.helpbot.sys.interaction.select;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.util.TemporaryRunnableStorage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.function.Consumer;

public class SelectMenuHandler {

    private static final TemporaryRunnableStorage<Long, SelectMenuListener> SELECT_CONSUMERS = new TemporaryRunnableStorage<>();

    public static void addListener(long user, Message message, Consumer<StringSelectInteractionEvent> consumer) {
        SELECT_CONSUMERS.put(message.getIdLong(), new SelectMenuListener(user, consumer), () -> message.delete().queue(null, (error) -> {}));
    }

    public static void handleEvent(StringSelectInteractionEvent event) {
        SelectMenuListener listener = SELECT_CONSUMERS.get(event.getMessageIdLong());
        if (listener == null) {
            return;
        }

        if (event.getUser().getIdLong() != listener.user) {
            event.replyEmbeds(new PresetBuilder().withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "You can't use this menu!"))
                    .getEmbed()
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        SELECT_CONSUMERS.expireKey(event.getMessageIdLong());
        listener.consumer.accept(event);
    }

    private record SelectMenuListener(long user, Consumer<StringSelectInteractionEvent> consumer) {

    }
}
