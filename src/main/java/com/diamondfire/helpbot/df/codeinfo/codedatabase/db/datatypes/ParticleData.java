package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.google.gson.JsonObject;

public class ParticleData extends CodeObject {

    public ParticleData(JsonObject data) {
        super(data, data.get("particle").getAsString());
    }

    public String getParticle() {
        return data.get("particle").getAsString();
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.PARTICLE;
    }
}
