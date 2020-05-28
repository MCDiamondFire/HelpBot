package com.diamondfire.helpbot.components.codedatabase.db.datatypes;

import com.diamondfire.helpbot.components.viewables.constants.DataTypes;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.JsonObject;

public class GameValueData extends SimpleData {

    /**
     * Creates a new game value, which represents the data of a game value.
     *
     * @param data The information
     */
    public GameValueData(JsonObject data) {
        super(data, new DisplayIconData(data.get("icon").getAsJsonObject()).getItemName());
    }

    /**
     * @return The aliases of a gamevalue, reasons unknown.
     */
    public String[] getAliases() {
        return Util.jsonArrayToString(this.data.get("aliases").getAsJsonArray());
    }

    /**
     * @return The category that gamevalue is located in.
     */
    public String getCategory() {
        return this.data.get("category").getAsString();
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.GAME_VALUE;
    }
}

