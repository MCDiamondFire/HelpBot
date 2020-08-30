package com.diamondfire.helpbot.df.punishments;

import com.diamondfire.helpbot.util.*;

import java.util.Date;

public class Punishment {

    public final PunishmentType type;
    public final String uuid;
    public final String reason;
    public final String staffUUID;
    public final String staffName;
    public final Date startTime;
    public final Date untilTime;
    public final boolean silent;
    public final boolean active;
    public final String removeByUUID;
    public final String removeByName;
    public final Date removeDate;

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

    private String formatExpire() {

        if (untilTime == null && active) return "Never";
        if (removeDate == null) return "?";
        if (removeByName == null) return FormatUtil.formatDate(removeDate);

        return FormatUtil.formatDate(removeDate) + (removeByName.equals("#expired") ? " Automatically" : " (Removed by staff)");
    }

    @Override
    public String toString() {
        String reasonGiven = reason.isBlank() ? "No Reason Given" : reason;
        String until = (untilTime == null ? "Never" :  FormatUtil.formatDate(untilTime));
        String expire = '\n' + (active ? "Expires " + until : "Expired " + formatExpire());
        if (type == PunishmentType.KICK) {
            expire = "";
        }

        return String.format("[%s] %s\n%s%s", type, FormatUtil.formatDate(startTime), reasonGiven.trim(), expire);
    }
}

