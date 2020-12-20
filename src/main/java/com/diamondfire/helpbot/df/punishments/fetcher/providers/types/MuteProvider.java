package com.diamondfire.helpbot.df.punishments.fetcher.providers.types;

import com.diamondfire.helpbot.df.punishments.PunishmentType;
import com.diamondfire.helpbot.df.punishments.fetcher.providers.RemovablePunishmentProvider;

public class MuteProvider extends RemovablePunishmentProvider {
    
    @Override
    public PunishmentType getType() {
        return PunishmentType.MUTE;
    }
    
    @Override
    public String getDBTable() {
        return "mutes";
    }
}
