package com.diamondfire.helpbot.bot.interaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.*;

public interface Interaction extends ISnowflake {

	@Nonnull
	JDA getJDA();

	@Nonnull
	InteractionType getType();

	@Nonnull
	Guild getGuild();

	@Nonnull
	GuildChannel getChannel();

	@Nonnull
	Member getMember();

	@Nonnull
	String getInteractionToken();

	@Nonnull @CheckReturnValue
	default RestAction<Void> acknowledge() {
		return acknowledge(true);
	}

	@Nonnull @CheckReturnValue
	RestAction<Void> acknowledge(boolean withSource);

	default RestAction<Void> respond() {
		return respond(true);
	}

	RestAction<Void> respond(boolean withSource);

	enum InteractionType {

		PING(1),
		APPLICATION_COMMAND(2);

		public final int id;

		InteractionType(int id) {
			this.id = id;
		}
	}
}
