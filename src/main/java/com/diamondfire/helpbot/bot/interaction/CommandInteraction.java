package com.diamondfire.helpbot.bot.interaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.*;
import net.dv8tion.jda.internal.requests.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandInteraction implements Interaction {

	private final JDA jda;
	private final long id;
	private final String token;
	private final Guild guild;
	private final GuildChannel channel;
	private final Member member;
	private final String name;
	private final OptionSet options;
	private final long commandId;

	public CommandInteraction(JDA jda, long id, String token, Guild guild, GuildChannel channel, Member member,
							  String name, OptionSet options, long commandId) {
		this.jda = jda;
		this.id = id;
		this.token = token;
		this.guild = guild;
		this.channel = channel;
		this.member = member;
		this.name = name;
		this.options = options;
		this.commandId = commandId;
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
	public String getCommandName() {
		return name;
	}

	@NotNull
	public Long getCommandId() {
		return id;
	}

	@NotNull
	public OptionSet getOptions() {
		return options;
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
				", command=Command{" +
				"id=" + id +
				", name=" + name +
				"}," +
				", options=" + options +
				"}";
	}

	public static class OptionSet {

		private final JDA jda;
		private final Map<String, Object> options;

		@SuppressWarnings("unchecked")
		public static OptionSet parse(JDA jda, DataArray data) {
			Map<String, Object> options = new HashMap<>();
			for (Object object : data) {
				Map<String, Object> optionData = (Map<String, Object>) object;
				String name = (String) optionData.get("name");

				if (optionData.containsKey("options")) {
					options.put(name, DataArray.fromCollection((Collection<?>) optionData.get("options")));
				} else {
					options.put(name, optionData.get("value"));
				}
			}
			return new OptionSet(jda, options);
		}

		private OptionSet(JDA jda, Map<String, Object> options) {
			this.jda = jda;
			this.options = options;
		}

		public JDA getJDA() {
			return jda;
		}

		public Optional<String> getString(String name) {
			return Optional.ofNullable(options.get(name)).map(value -> (String) value);
		}

		public Optional<Integer> getInt(String name) {
			Optional<String> stringValue = getString(name);
			return stringValue.map(Integer::parseInt);
		}

		public Optional<Boolean> getBoolean(String name) {
			return Optional.ofNullable(options.get(name)).map(value -> (Boolean) value);
		}

		public Optional<User> getUser(String name) {
			return getString(name).map(jda::getUserById);
		}

		public RestAction<User> retrieveUser(String name) {
			return jda.retrieveUserById(getString(name).orElseThrow());
		}

		public Optional<GuildChannel> getChannel(String name) {
			return getString(name).map(jda::getGuildChannelById);
		}

		public Optional<Role> getRole(String name) {
			return getString(name).map(jda::getRoleById);
		}

		public Optional<OptionSet> getSubCommand(String name) {
			return Optional.ofNullable(options.get(name)).map(value -> (OptionSet) value);
		}

		@Override
		public String toString() {
			return options.toString();
		}
	}
}
