package com.owen1212055.helpbot.components.codedatabase.db.datatypes;

import com.google.gson.JsonObject;
import com.owen1212055.helpbot.components.viewables.consts.DataTypes;


public class PotionData extends SimpleData {


    /**
     * Creates a new potion, which represents the data of a potion.
     *
     * @param data The information
     */
    public PotionData(JsonObject data) {
        super(data, data.get("potion").getAsString());
    }


    /**
     * @return The type of potion.
     */
    public String getPotion() {
        return this.data.get("potion").getAsString();
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.POTION;
    }
}

