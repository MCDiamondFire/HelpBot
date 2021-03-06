package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.google.gson.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class MinecraftPlayerUUIDArgument extends AbstractSimpleValueArgument<UUID> {
    
    @Override
    protected UUID parse(@NotNull String argument) throws ArgumentException {
        if (argument.contains("-") || argument.length() > 16) {
            return UUID.fromString(argument);
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
                return UUID.fromString(responseObject.get("id").getAsString());
            } else {
                return null;
            }
        }
    }
}
