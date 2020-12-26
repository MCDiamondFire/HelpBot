package com.diamondfire.helpbot.bot.interaction;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.*;
import net.dv8tion.jda.internal.requests.*;
import net.dv8tion.jda.internal.requests.Route;
import okhttp3.*;

import java.util.*;

public class InteractionResponseAction extends RestActionImpl<Void> implements RestAction<Void> {

	private CharSequence content = null;
	private final List<MessageEmbed> embeds = new ArrayList<>();
	private boolean showSource = false;
	private int flags = 0;
	private boolean tts = false;

	public InteractionResponseAction(JDA api, Route.CompiledRoute route) {
		super(api, route);
	}

	public InteractionResponseAction(JDA api, Route.CompiledRoute route, DataObject data) {
		super(api, route, data);
	}

	public InteractionResponseAction(JDA api, Route.CompiledRoute route, RequestBody data) {
		super(api, route, data);
	}

	public InteractionResponseAction setContent(CharSequence content) {
		this.content = content;
		return this;
	}

	public InteractionResponseAction addEmbed(MessageEmbed embed) {
		if (embeds.size() >= 10) throw new IllegalArgumentException("Cannot add more than 10 embeds.");
		this.embeds.add(embed);
		return this;
	}

	public InteractionResponseAction showSource(boolean showSource) {
		this.showSource = showSource;
		return this;
	}


	public InteractionResponseAction setEphemeral(boolean ephemeral) {
		this.flags |= (1 << 6);
		return this;
	}

	public InteractionResponseAction setTts(boolean tts) {
		this.tts = tts;
		return this;
	}

	@Override
	protected RequestBody finalizeData() {
		DataObject body = DataObject.empty()
				.put("type", showSource ? 4 : 3)
				.put("data", DataObject.empty()
						.put("content", content)
						.put("embeds", DataArray.fromCollection(embeds))
						.put("flags", flags));

		return RequestBody.create(Requester.MEDIA_TYPE_JSON, body.toJson());
	}
}
