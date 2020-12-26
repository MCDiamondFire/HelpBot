package com.diamondfire.helpbot.bot.interaction;

import net.dv8tion.jda.internal.requests.Route;

public class InteractionRoute {

	public static final Route CREATE_RESPONSE = Route.post("interactions/{interaction_id}/{interaction_token}/callback");
	public static final Route EDIT_RESPONSE = Route.patch("interactions/{interaction_id}/{interaction_token}/messages/@original");
	public static final Route DELETE_RESPONSE = Route.delete("interactions/{interaction_id}/{interaction_token}/messages/@original");

	public static final Route CREATE_FOLLOWUP = Route.post("interactions/{interaction_id}/{interaction_token}");
	public static final Route EDIT_FOLLOWUP = Route.patch("interactions/{interaction_id}/{interaction_token}/messages/{message_id}");
	public static final Route DELETE_FOLLOWUP = Route.patch("interactions/{interaction_id}/{interaction_token}/messages/{message_id}");
}
