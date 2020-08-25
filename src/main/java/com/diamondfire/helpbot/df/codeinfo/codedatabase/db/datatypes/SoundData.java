package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.google.gson.JsonObject;


public class SoundData extends CodeObject {

    public SoundData(JsonObject data) {
        super(data, data.get("sound").getAsString());
    }

    public String getSound() {
        return data.get("sound").getAsString();
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.SOUND;
    }
}
