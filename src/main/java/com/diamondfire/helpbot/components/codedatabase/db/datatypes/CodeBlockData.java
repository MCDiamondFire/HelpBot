package com.diamondfire.helpbot.components.codedatabase.db.datatypes;

import com.diamondfire.helpbot.components.viewables.constants.CodeBlockEnum;
import com.diamondfire.helpbot.components.viewables.constants.DataTypes;
import com.google.gson.JsonObject;

public class CodeBlockData extends SimpleData {

    private CodeBlockActionData assosiatedAction;

    /**
     * Creates a main codeblock that goes into the players inventory.
     *
     * @param data The information
     */
    public CodeBlockData(JsonObject data) {
        super(data, data.get("name").getAsString());
    }

    /**
     * @return The name of the codeblock.
     */
    public String getName() {
        return this.data.get("name").getAsString();
    }

    /**
     * @return The identifier of the codeblock. (internal ID)
     */
    public String getIdentifier() {
        return this.data.get("identifier").getAsString();
    }

    /**
     * @return The item of the codeblock that is in the players inventory.
     */
    @Override
    public DisplayIconData getItem() {
        return new DisplayIconData(this.data.get("item").getAsJsonObject());
    }

    public void assignAction(CodeBlockActionData data) {
        assosiatedAction = data;
    }

    public CodeBlockActionData getAssociatedAction() {
        return assosiatedAction;
    }

    public CodeBlockEnum getCodeblockEnum() {
        return CodeBlockEnum.getFromID(getIdentifier());
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.CODEBLOCK;
    }


}
