package com.diamondfire.helpbot.components.codedatabase.db.datatypes;

import com.diamondfire.helpbot.components.viewables.consts.DataTypes;
import com.google.gson.JsonObject;


public class SoundData extends SimpleData {

    /**
     * Creates a new sound, which represents the data of a sound.
     *
     * @param data The information
     */
    public SoundData(JsonObject data) {
        super(data, data.get("sound").getAsString());
    }

    /**
     * @return The type of sound that is played.
     */
    public String getSound() {
        return this.data.get("sound").getAsString();
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.SOUND;
    }
}
