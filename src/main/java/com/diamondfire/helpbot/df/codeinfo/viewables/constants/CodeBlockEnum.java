package com.diamondfire.helpbot.df.codeinfo.viewables.constants;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.Emote;

import java.awt.*;
import java.util.Map;
import java.util.stream.*;

public enum CodeBlockEnum {
    PLAYER_EVENT("event", new Color(0x3DE0E5)),
    ENTITY_EVENT("entity_event", new Color(0xF5CC28)),
    FUNCTION("func", new Color(0x1C3890)),
    PROCESS("process", new Color(0x16DD61)),
    // Conditionals
    IF_PLAYER("if_player", new Color(0xAF8F55)),
    IF_ENTITY("if_entity", new Color(0x9A5643)),
    IF_GAME("if_game", new Color(0x440507)),
    IF_VARIABLE("if_var", new Color(0x261E3D)),
    ELSE("else", new Color(0xD5DA94)),
    // Game Manipulators
    PLAYER_ACTION("player_action", new Color(0x616161)),
    ENTITY_ACTION("entity_action", new Color(0x728552)),
    GAME_ACTION("game_action", new Color(0x511515)),
    // Code Manipulators
    SELECT_OBJECT("select_obj", new Color(0xA472A2)),
    SET_VARIABLE("set_var", new Color(0xE0E0E0)),
    CONTROL("control", new Color(0x151515)),
    REPEAT("repeat", new Color(0x5EA496)),
    // Line Starters
    CALL_FUNC("call_func", new Color(0x1C3890)),
    START_PROCESS("start_process", new Color(0x16DD61));
    
    private static final Map<String, CodeBlockEnum> identifiers = Stream.of(CodeBlockEnum.values()).collect(Collectors.toMap(CodeBlockEnum::getID, p -> p));
    private final String ID;
    private final Color color;
    
    CodeBlockEnum(String ID, Color color) {
        this.ID = ID;
        this.color = color;
    }
    
    public static CodeBlockEnum getFromID(String id) {
        return identifiers.getOrDefault(id, CodeBlockEnum.CONTROL);
    }
    
    public Color getColor() {
        return color;
    }
    
    public String getID() {
        return ID;
    }
    
    
    public Emote getEmoji() {
        return HelpBotInstance.getJda().getEmotesByName(getID(), true).get(0);
    }
}
