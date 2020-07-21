package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.google.gson.JsonObject;

public abstract class SimpleData {

    protected final JsonObject data;
    protected final String mainName;

    public SimpleData(JsonObject data, String mainName) {

        this.data = data;
        this.mainName = mainName;
    }

    /**
     * @return The icon that the data appears as.
     */
    public DisplayIconData getItem() {
        return new DisplayIconData(this.data.get("icon").getAsJsonObject());
    }

    public String getMainName() {
        return mainName;
    }

    public abstract DataTypes getEnum();

    public JsonObject getJson() {
        return data;
    }
}
