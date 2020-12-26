package com.diamondfire.helpbot.bot.interaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.*;
import org.jetbrains.annotations.NotNull;

public class CommandInteraction implements Interaction {

	private final JDA jda;
	private final long id;
	private final String token;
	private final Guild guild;
	private final GuildChannel channel;
	private final Member member;

	public CommandInteraction(JDA jda, long id, String token, Guild guild, GuildChannel channel, Member member) {
		this.jda = jda;
		this.id = id;
		this.token = token;
		this.guild = guild;
		this.channel = channel;
		this.member = member;
	}

	@Override
	public long getIdLong() {
		return id;
	}

	@NotNull
	@Override
	public JDA getJDA() {
		return jda;
	}

	@NotNull
	@Override
	public InteractionType getType() {
		return InteractionType.APPLICATION_COMMAND;
	}

	@NotNull
	@Override
	public Guild getGuild() {
		return guild;
	}

	@NotNull
	@Override
	public GuildChannel getChannel() {
		return channel;
	}

	@NotNull
	@Override
	public Member getMember() {
		return member;
	}

	@NotNull
	@Override
	public String getInteractionToken() {
		return token;
	}

	@NotNull
	@Override
	public RestAction<Void> acknowledge() {
		return acknowledge(false);
	}

	@NotNull
	@Override
	public RestAction<Void> acknowledge(boolean withSource) {
		Route.CompiledRoute route = InteractionRoute.CREATE_RESPONSE.compile(String.valueOf(id), token);
		DataObject body = DataObject.empty()
				.put("type", withSource ? 5 : 2);

		return new RestActionImpl<>(jda, route, body);
	}

	@Override
	public RestAction<Void> respond() {
		return null;
	}

	@Override
	public RestAction<Void> respond(boolean withSource) {
		return null;
	}

	@Override
	public String toString() {
		return "CommandInteraction{" +
				"id=" + id +
				", token='" + token + '\'' +
				", guild=" + guild +
				", channel=" + channel +
				", member=" + member +
				'}';
	}
}
