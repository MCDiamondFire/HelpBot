package com.diamondfire.helpbot.df.punishments.fetcher;

import com.diamondfire.helpbot.df.punishments.Punishment;
import com.diamondfire.helpbot.df.punishments.fetcher.providers.PunishmentProvider;
import com.diamondfire.helpbot.df.punishments.fetcher.providers.types.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;

import java.sql.ResultSet;
import java.util.*;

public class PunishmentFetcher {
    
    public static final PunishmentProvider WARN = new WarnProvider();
    public static final PunishmentProvider KICK = new KickProvider();
    public static final PunishmentProvider MUTE = new MuteProvider();
    public static final PunishmentProvider BAN = new BanProvider();
    
    private String uuid;
    private PunishmentProvider type;
    private boolean all;
    
    
    public PunishmentFetcher withUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }
    
    public PunishmentFetcher withAll() {
        this.all = true;
        return this;
    }
    
    public PunishmentFetcher withType(PunishmentProvider type) {
        this.type = type;
        return this;
    }
    
    public List<Punishment> fetch() {
        List<Punishment> punishments = new ArrayList<>();
        
        if (all) {
            punishments.addAll(getPunishments(WARN));
            punishments.addAll(getPunishments(KICK));
            punishments.addAll(getPunishments(MUTE));
            punishments.addAll(getPunishments(BAN));
        } else {
            punishments.addAll(getPunishments(type));
        }
        
        punishments.sort(Comparator.comparing(punish -> punish.startTime));
        Collections.reverse(punishments);
        return punishments;
    }
    
    private List<Punishment> getPunishments(PunishmentProvider provider) {
        List<Punishment> punishments = new ArrayList<>();
        
        new DatabaseQuery()
                .query(new BasicQuery(provider.getQuery(), (statement) -> statement.setString(1, uuid)))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        punishments.add(provider.getPunishment(set));
                    }
                });
        
        return punishments;
    }
}
