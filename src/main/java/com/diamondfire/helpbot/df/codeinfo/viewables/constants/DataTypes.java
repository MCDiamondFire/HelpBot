package com.diamondfire.helpbot.df.codeinfo.viewables.constants;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.df.codeinfo.viewables.embeds.*;

public enum DataTypes {
    CODEBLOCK("Codeblock", 688643535842705408L, new CodeBlockEmbedBuilder()),
    ACTION("Codeblock Action", 688643679686230066L, new CodeActionEmbedBuilder()),
    GAME_VALUE("Game Value", 789274945850441728L, new GameValueEmbedBuilder()),
    POTION("Potion", 789274945162575912L, new SimpleIconBuilder()),
    PARTICLE("Particle Effect", 789274944730824796L, new SimpleIconBuilder()),
    SOUND("Sound Effect", 789274945187479563L, new SimpleIconBuilder());
    
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
