package com.diamondfire.helpbot.df.punishments.fetcher.providers.types;

import com.diamondfire.helpbot.df.punishments.PunishmentType;
import com.diamondfire.helpbot.df.punishments.fetcher.providers.RemovablePunishmentProvider;

public class BanProvider extends RemovablePunishmentProvider {

    @Override
    public PunishmentType getType() {
        return PunishmentType.BAN;
    }

    @Override
    public String getDBTable() {
        return "bans";
    }
}
