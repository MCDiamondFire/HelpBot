package com.diamondfire.helpbot.bot.interaction.event;

import com.diamondfire.helpbot.bot.interaction.*;
import com.diamondfire.helpbot.bot.interaction.CommandInteraction.OptionSet;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.hooks.*;
import net.dv8tion.jda.api.utils.data.*;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.jetbrains.annotations.NotNull;

public class InteractionDispatcher extends ListenerAdapter {

	@Override
	public void onRawGateway(@NotNull RawGatewayEvent event) {
		if (!event.getType().equals("INTERACTION_CREATE")) return;

		DataObject payload = event.getPayload();
		DataObject data = payload.getObject("data");

		// future-proofing for future interactions
		if (payload.getInt("type") == Interaction.InteractionType.APPLICATION_COMMAND.id) {
			JDAImpl jda = (JDAImpl) event.getJDA();
			GuildImpl guild = (GuildImpl) jda.getGuildById(payload.getLong("guild_id"));

			DataArray options = data.hasKey("options") ? data.getArray("options") : DataArray.empty();

			CommandInteraction interaction = new CommandInteraction(jda, payload.getLong("id"),
					payload.getString("token"), jda.getGuildById(payload.getLong("guild_id")),
					jda.getGuildChannelById(payload.getLong("channel_id")),
					jda.getEntityBuilder().createMember(guild, payload.getObject("member")),
					data.getString("name"), OptionSet.parse(jda, options),
					data.getLong("id")
			);

			jda.getEventManager().handle(new CommandInteractionEvent(jda, event.getResponseNumber(), interaction));
		}
	}

	@Override
	public void onGenericEvent(@NotNull GenericEvent genericEvent) {
		if (genericEvent instanceof CommandInteractionEvent) {
			CommandInteractionEvent event = (CommandInteractionEvent) genericEvent;
			CommandInteraction interaction = event.getInteraction();
			interaction.respond("Your options were: " + interaction.getOptions())
					.showSource(true)
					.setEphemeral(true)
					.queue();
		}
	}
}
