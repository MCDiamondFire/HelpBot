package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.diamondfire.helpbot.util.Util;
import com.google.gson.JsonObject;

public class GameValueData extends CodeObject {
    
    public GameValueData(JsonObject data) {
        super(data, new DisplayIcon(data.get("icon").getAsJsonObject()).getItemName());
    }
    
    public String[] getAliases() {
        return Util.jsonArrayToString(data.get("aliases").getAsJsonArray());
    }
    
    public String getCategory() {
        return data.get("category").getAsString();
    }
    
    @Override
    public DataTypes getEnum() {
        return DataTypes.GAME_VALUE;
    }
}

