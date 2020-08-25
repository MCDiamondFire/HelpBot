package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.*;
import com.google.gson.JsonObject;

public class CodeBlockData extends CodeObject {

    private ActionData associatedAction;

    public CodeBlockData(JsonObject data) {
        super(data, data.get("name").getAsString());
    }

    public String getName() {
        return data.get("name").getAsString();
    }

    public String getIdentifier() {
        return data.get("identifier").getAsString();
    }

    @Override
    public DisplayIcon getItem() {
        return new DisplayIcon(data.get("item").getAsJsonObject());
    }

    public void assignAction(ActionData data) {
        associatedAction = data;
    }

    public ActionData getAssociatedAction() {
        return associatedAction;
    }

    public CodeBlockEnum getCodeblockEnum() {
        return CodeBlockEnum.getFromID(getIdentifier());
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.CODEBLOCK;
    }

}
