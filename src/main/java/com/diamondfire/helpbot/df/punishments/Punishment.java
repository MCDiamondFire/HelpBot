package com.diamondfire.helpbot.df.punishments;

import com.diamondfire.helpbot.util.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public class Punishment {

    public final PunishmentType type;
    public final String uuid;
    public final String reason;
    public final String staffUUID;
    public final String staffName;
    public final Timestamp startTime;
    public final Timestamp untilTime;
    public final boolean silent;
    public final boolean active;
    public final String removeByUUID;
    public final String removeByName;
    public final Timestamp removeDate;

    public Punishment(PunishmentType type, String uuid, String reason, String staffUUID, String staffName, Timestamp startTime, Timestamp untilTime, boolean silent, boolean active, String removeByUUID, String removeByName, Timestamp removeDate) {
        this.type = type;
        this.uuid = uuid;
        this.reason = StringUtil.display(reason);
        this.staffUUID = staffUUID;
        this.staffName = staffName;
        this.startTime = startTime;
        this.untilTime = untilTime;
        this.silent = silent;
        if (untilTime == null) {
            this.active = active;
        } else {
            this.active = (active || untilTime.toInstant().isAfter(Instant.now()));
        }
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
        String until = (untilTime == null ? "Never" : FormatUtil.formatDate(untilTime));
        String expire = '\n' + (active ? "Expires " + until : "Expired " + formatExpire());
        if (type == PunishmentType.KICK) {
            expire = "";
        }

        return String.format("[%s] %s\n%s%s", type, FormatUtil.formatDate(startTime), reasonGiven.trim(), expire);
    }
}

