package com.diamondfire.helpbot.bot.interaction.event;

import com.diamondfire.helpbot.bot.interaction.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.jetbrains.annotations.NotNull;

public class InteractionDispatcher implements EventListener {

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof RawGatewayEvent) {

			RawGatewayEvent rawEvent = (RawGatewayEvent) event;
			if (!(rawEvent.getType().equals("INTERACTION_CREATE"))) return;

			DataObject payload = rawEvent.getPayload();

			// futureproofing for future interactions
			if (payload.getInt("type") == Interaction.InteractionType.APPLICATION_COMMAND.id) {
				JDAImpl jda = (JDAImpl) event.getJDA();
				GuildImpl guild = (GuildImpl) jda.getGuildById(payload.getLong("guild_id"));

				CommandInteraction interaction = new CommandInteraction(jda, payload.getLong("id"),
						payload.getString("token"), jda.getGuildById(payload.getLong("guild_id")),
						jda.getGuildChannelById(payload.getLong("channel_id")),
						jda.getEntityBuilder().createMember(guild, payload.getObject("member")));

				jda.getEventManager().handle(new CommandInteractionEvent(jda, event.getResponseNumber(), interaction));
			}

			return;
		}

		if (event instanceof CommandInteractionEvent) {
			CommandInteractionEvent commandEvent = (CommandInteractionEvent) event;
			commandEvent.getInteraction().acknowledge(true).queue();
		}
	}
}
