package com.diamondfire.helpbot.bot.command.argument.impl.types.impl.minecraft;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

// TODO: Player
public class MojangPlayerUUIDArgument extends AbstractSimpleValueArgument<UUID> {
    
    @Override
    protected UUID parse(@NotNull String argument, CommandEvent event) throws ArgumentException {
        if (argument.length() > 16) {
           return Util.toUuid(argument);
        } else {
            JsonObject responseObject = null;
            ResponseBody res = null;
            Request request = new Request.Builder().url("https://api.mojang.com/users/profiles/minecraft/" + argument).get().build();
            try {
                res = HelpBotInstance.getHttpClient().newCall(request).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try {
                responseObject = JsonParser.parseString(res.string()).getAsJsonObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if (responseObject.has("id")) {
                return Util.toUuid(responseObject.get("id").getAsString());
            } else {
                return null;
            }
        }
    }
}
