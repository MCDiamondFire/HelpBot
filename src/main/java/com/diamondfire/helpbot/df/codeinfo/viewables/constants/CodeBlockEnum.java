package com.diamondfire.helpbot.df.codeinfo.viewables.constants;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.emoji.*;

import java.awt.*;
import java.util.Map;
import java.util.stream.*;

public enum CodeBlockEnum {
    PLAYER_EVENT("event", "3DE0E5"),
    ENTITY_EVENT("entity_event", "F5CC28"),
    FUNCTION("func", "1C3890"),
    PROCESS("process", "16DD61"),
    // Conditionals
    IF_PLAYER("if_player", "AF8F55"),
    IF_ENTITY("if_entity", "9A5643"),
    IF_GAME("if_game", "440507"),
    IF_VARIABLE("if_var", "261E3D"),
    ELSE("else", "D5DA94"),
    // Game Manipulators
    PLAYER_ACTION("player_action", "616161"),
    ENTITY_ACTION("entity_action", "728552"),
    GAME_ACTION("game_action", "511515"),
    // Code Manipulators
    SELECT_OBJECT("select_obj", "A472A2"),
    SET_VARIABLE("set_var", "E0E0E0"),
    CONTROL("control", "151515"),
    REPEAT("repeat", "5EA496"),
    // Line Starters
    CALL_FUNC("call_func", "1C3890"),
    START_PROCESS("start_process", "16DD61");
    
    private static final Map<String, CodeBlockEnum> identifiers = Stream.of(CodeBlockEnum.values()).collect(Collectors.toMap(CodeBlockEnum::getID, p -> p));
    private final String ID;
    private final String color;
    
    CodeBlockEnum(String ID, String color) {
        this.ID = ID;
        this.color = color;
    }
    
    public static CodeBlockEnum getFromID(String id) {
        return identifiers.getOrDefault(id, CodeBlockEnum.CONTROL);
    }
    
    public Color getColor() {
        return Color.decode("#" + color);
    }
    
    public String getID() {
        return ID;
    }
    
    
    public CustomEmoji getEmoji() {
        return HelpBotInstance.getJda().getEmojisByName(getID(), true).get(0);
    }
}
