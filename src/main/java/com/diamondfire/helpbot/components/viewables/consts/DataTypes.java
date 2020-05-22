package com.diamondfire.helpbot.components.viewables.consts;

import com.diamondfire.helpbot.components.viewables.embeds.*;
import com.owen1212055.helpbot.components.viewables.embeds.*;

public enum DataTypes {
    CODEBLOCK("Codeblock", 688643535842705408L, new CodeBlockEmbedBuilder()),
    ACTION("Codeblock Action", 688643679686230066L, new CodeActionEmbedBuilder()),
    GAME_VALUE("Game Value", 701240459908874331L, new GameValueEmbedBuilder()),
    POTION("Potion", 688648359736508471L, new IconEmbedBuilder()),
    PARTICLE("Particle Effect", 688648334864416775L, new IconEmbedBuilder()),
    SOUND("Sound Effect", 688648383816269854L, new IconEmbedBuilder());

    private final String name;
    private final long emoji;
    private final DataEmbedBuilder embedBuilder;

    DataTypes(String name, long emoji, DataEmbedBuilder builder) {
        this.name = name;
        this.emoji = emoji;
        this.embedBuilder = builder;
    }

    public String getName() {
        return name;
    }

    public long getEmoji() {
        return emoji;
    }

    public DataEmbedBuilder getEmbedBuilder() {
        return embedBuilder;
    }
}
