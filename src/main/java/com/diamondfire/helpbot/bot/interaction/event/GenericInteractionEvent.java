package com.diamondfire.helpbot.bot.interaction.event;

import com.diamondfire.helpbot.bot.interaction.Interaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class GenericInteractionEvent extends GenericGuildEvent {

	private final Interaction interaction;

	public GenericInteractionEvent(@NotNull JDA api, long responseNumber, @NotNull Interaction interaction) {
		super(api, responseNumber, interaction.getGuild());
		this.interaction = interaction;
	}

	@Nonnull
	public Interaction getInteraction() {
		return interaction;
	}
}
