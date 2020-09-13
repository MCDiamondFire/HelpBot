package com.diamondfire.helpbot.df.codeinfo.viewables.constants;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.df.codeinfo.viewables.embeds.*;

public enum DataTypes {
    CODEBLOCK("Codeblock", 688643535842705408L, new CodeBlockEmbedBuilder()),
    ACTION("Codeblock Action", 688643679686230066L, new CodeActionEmbedBuilder()),
    GAME_VALUE("Game Value", 701240459908874331L, new GameValueEmbedBuilder()),
    POTION("Potion", 688648359736508471L, new SimpleIconBuilder()),
    PARTICLE("Particle Effect", 688648334864416775L, new SimpleIconBuilder()),
    SOUND("Sound Effect", 688648383816269854L, new SimpleIconBuilder());

    private final String name;
    private final long emoji;
    private final IconEmbedBuilder<? extends CodeObject> embedBuilder;

    DataTypes(String name, long emoji, IconEmbedBuilder<? extends CodeObject> builder) {
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

    public <T extends CodeObject> IconEmbedBuilder<T> getEmbedBuilder() {
        return (IconEmbedBuilder<T>) embedBuilder;
    }
}
