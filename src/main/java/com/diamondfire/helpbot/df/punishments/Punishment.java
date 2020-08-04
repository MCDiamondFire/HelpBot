package com.diamondfire.helpbot.df.punishments;

import com.diamondfire.helpbot.df.punishments.fetcher.providers.StaticPunishmentProvider;
import com.diamondfire.helpbot.df.punishments.fetcher.providers.types.KickProvider;
import com.diamondfire.helpbot.util.StringUtil;

import java.util.Date;

public class Punishment {

    public PunishmentType type;
    public String uuid;
    public String reason;
    public String staffUUID;
    public String staffName;
    public Date startTime;
    public Date untilTime;
    public boolean silent;
    public boolean active;
    public String removeByUUID;
    public String removeByName;
    public Date removeDate;

    public Punishment(PunishmentType type, String uuid, String reason, String staffUUID, String staffName, Date startTime, Date untilTime, boolean silent, boolean active, String removeByUUID, String removeByName, Date removeDate) {
        this.type = type;
        this.uuid = uuid;
        this.reason = StringUtil.display(reason);
        this.staffUUID = staffUUID;
        this.staffName = staffName;
        this.startTime = startTime;
        this.untilTime = untilTime;
        this.silent = silent;
        this.active = active;
        this.removeByUUID = removeByUUID;
        this.removeByName = removeByName;
        this.removeDate = removeDate;
    }

    @Override
    public String toString() {
        String given = "Given " + StringUtil.formatDate(startTime);
        String reasonGiven = reason.isBlank() ? "No Reason Given" : reason;
        String expire = (active ? "Expires " : "Expired ") + formatUntil(untilTime);
        String duration = "";

        if (type == PunishmentType.KICK) {
            expire = "";
        }

        if (untilTime != null && (type == PunishmentType.BAN || type == PunishmentType.MUTE)) {
            duration = String.format("(Duration %s)", StringUtil.formatMilliTime(untilTime.getTime() - startTime.getTime()));
        }

       return String.format("[%s] %s %s %s \n%s", type, given, expire, duration, reasonGiven);
    }

    private static String formatUntil(Date date) {
        if (date == null) return "Never";

        return StringUtil.formatDate(date);
    }
}

