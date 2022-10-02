package com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import com.google.gson.JsonObject;
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
            
            try {
                responseObject = WebUtil.getJson("https://api.mojang.com/users/profiles/minecraft/" + argument).getAsJsonObject();
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
