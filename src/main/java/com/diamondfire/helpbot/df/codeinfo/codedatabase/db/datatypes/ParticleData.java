package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.google.gson.JsonObject;

public class ParticleData extends SimpleData {

    /**
     * Creates a new particle, which represents the data of a particle.
     *
     * @param data The information
     */
    public ParticleData(JsonObject data) {
        super(data, data.get("particle").getAsString());
    }

    /**
     * @return The type of particle
     */
    public String getParticle() {
        return this.data.get("particle").getAsString();
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.PARTICLE;
    }
}
