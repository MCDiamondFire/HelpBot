package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.google.gson.JsonObject;


public class PotionData extends CodeObject {
    
    public PotionData(JsonObject data) {
        super(data, data.get("potion").getAsString());
    }
    
    public String getPotion() {
        return data.get("potion").getAsString();
    }
    
    @Override
    public DataTypes getEnum() {
        return DataTypes.POTION;
    }
}

