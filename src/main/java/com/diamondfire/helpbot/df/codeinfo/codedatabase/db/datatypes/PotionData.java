package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.google.gson.JsonObject;


public class PotionData extends CodeObject {
    
    public PotionData(JsonObject data) {
        super(data, data.has("potion") ? data.get("potion").getAsString() : data.get("potionId").getAsString());
    }
    
    public String getPotion() {
        return getName();
    }
    
    @Override
    public DataTypes getEnum() {
        return DataTypes.POTION;
    }
}

