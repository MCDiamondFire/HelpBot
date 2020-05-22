package com.owen1212055.helpbot.components.codedatabase.db.datatypes;

import com.google.gson.JsonObject;
import com.owen1212055.helpbot.components.viewables.consts.DataTypes;

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
