package com.owen1212055.helpbot.components.viewables.consts;

import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CodeBlockEnum {
    PLAYER_EVENT("event", "3DE0E5", 688643535842705408L),
    ENTITY_EVENT("entity_event", "F5CC28", 688643610228424744L),
    FUNCTION("func", "1C3890", 688643754806345745L),
    PROCESS("process", "16DD61", 688643947563712512L),
    // Conditionals
    IF_PLAYER("if_player", "AF8F55", 688643520457998397L),
    IF_ENTITY("if_entity", "9A5643", 688643580272836638L),
    IF_GAME("if_game", "440507", 688643798871310347L),
    IF_VARIABLE("if_var", "261E3D", 688643836557131797L),
    ELSE("else", "D5DA94", 688643730655281182L),
    // Game Manipulators
    PLAYER_ACTION("player_action", "616161", 688643495724056674L),
    ENTITY_ACTION("entity_action", "728552", 688643555354607636L),
    GAME_ACTION("game_action", "511515", 688643767674077191L),
    // Code Manipulators
    SELECT_OBJECT("select_obj", "A472A2", 688643980791251021L),
    SET_VARIABLE("set_var", "E0E0E0", 688643997891035166L),
    CONTROL("control", "151515", 688643713937047560L),
    REPEAT("repeat", "5EA496", 688643965540499456L),
    // Line Starters
    CALL_FUNC("call_func", "1C3890", 688643632063840281L),
    START_PROCESS("start_process", "16DD61", 688643646425268301L);

    private static Map<String, CodeBlockEnum> identifiers = Stream.of(CodeBlockEnum.values()).collect(Collectors.toMap(CodeBlockEnum::getID, p -> p));
    private final String ID;
    private final String color;
    private final long emoji;

    CodeBlockEnum(String ID, String color, long emoji) {
        this.ID = ID;
        this.color = color;
        this.emoji = emoji;
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

    public long getEmoji() {
        return emoji;
    }

}
