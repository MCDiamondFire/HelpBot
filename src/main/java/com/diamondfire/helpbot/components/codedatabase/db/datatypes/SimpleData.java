package com.diamondfire.helpbot.components.codedatabase.db.datatypes;

import com.diamondfire.helpbot.components.viewables.consts.DataTypes;
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
}
