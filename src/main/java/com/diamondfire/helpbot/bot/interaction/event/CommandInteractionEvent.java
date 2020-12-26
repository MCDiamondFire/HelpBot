package com.diamondfire.helpbot.bot.interaction.event;

import com.diamondfire.helpbot.bot.interaction.CommandInteraction;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class CommandInteractionEvent extends GenericInteractionEvent {

	public CommandInteractionEvent(@NotNull JDA api, long responseNumber, @NotNull CommandInteraction interaction) {
		super(api, responseNumber, interaction);
	}

	@Nonnull
	@Override
	public CommandInteraction getInteraction() {
		return (CommandInteraction) super.getInteraction();
	}
}
