package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.JsonObject;

public class CodeBlockActionData extends SimpleData {

    /**
     * A codeblock action, which can be selected from a main block.
     *
     * @param data The information
     */
    public CodeBlockActionData(JsonObject data) {
        super(data, data.get("name").getAsString().trim());
    }

    /**
     * @return The main name of the action. (This is used in sign form!!)
     */
    public String getName() {
        return this.data.get("name").getAsString().trim();
    }

    /**
     * @return The codeblock that the action is associated with.
     */
    public String getCodeblockName() {
        return this.data.get("codeblockName").getAsString();
    }

    /**
     * @return The tags that the action has.
     */
    public CodeBlockTagData[] getTags() {
        return CodeBlockTagData.parse(this.data.get("tags").getAsJsonArray());
    }

    /**
     * @return The aliases of the action which are typically used for blocks like Select Object and Repeat. (Special cases)
     */
    public String[] getAliases() {
        return Util.jsonArrayToString(this.data.get("aliases").getAsJsonArray());
    }

    public CodeBlockData getCodeBlockData() {
        return CodeDatabase.getCodeBlocks().stream()
                .filter((codeBlockData) -> codeBlockData.getName().equals(getCodeblockName()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.ACTION;
    }


}
