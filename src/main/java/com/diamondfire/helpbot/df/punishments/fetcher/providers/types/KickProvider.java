package com.diamondfire.helpbot.df.punishments.fetcher.providers.types;

import com.diamondfire.helpbot.df.punishments.PunishmentType;
import com.diamondfire.helpbot.df.punishments.fetcher.providers.StaticPunishmentProvider;

public class KickProvider extends StaticPunishmentProvider {

    @Override
    public PunishmentType getType() {
        return PunishmentType.KICK;
    }

    @Override
    public String getDBTable() {
        return "kicks";
    }
}
