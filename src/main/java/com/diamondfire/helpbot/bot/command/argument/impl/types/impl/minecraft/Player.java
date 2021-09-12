package com.diamondfire.helpbot.bot.command.argument.impl.types.impl.minecraft;

import java.util.UUID;

public record Player(String name, UUID uuid) {
    
    public String uuidString() {
        return uuid.toString();
    }
    
}
