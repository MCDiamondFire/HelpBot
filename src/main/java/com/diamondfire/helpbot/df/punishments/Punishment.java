package com.diamondfire.helpbot.df.punishments;

import com.diamondfire.helpbot.util.*;

import net.dv8tion.jda.api.utils.TimeFormat;

import java.sql.Timestamp;
import java.time.Instant;

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
    
    /**
     * Discord markdown view of this punishment, using timestamps that render in the viewer's timezone.
     */
    public String format() {
        StringBuilder builder = new StringBuilder();
        builder.append("**").append(typeName()).append("**");
        if (untilTime != null && (type == PunishmentType.MUTE || type == PunishmentType.BAN)) {
            builder.append(" (").append(FormatUtil.formatMilliTime(untilTime.getTime() - startTime.getTime())).append(")");
        } else if (untilTime == null && (type == PunishmentType.MUTE || type == PunishmentType.BAN)) {
            builder.append(" (Permanent)");
        }
        builder.append(" · ").append(TimeFormat.DATE_SHORT.format(startTime.getTime()));
        
        String reasonGiven = reason.isBlank() ? "*No reason given*" : reason.trim().replace("\n", " ").replace("\r", "");
        builder.append("\n> ").append(reasonGiven);
        
        String status = formatStatus();
        if (status != null) {
            builder.append("\n*").append(status).append("*");
        }
        
        return builder.toString();
    }
    
    private String typeName() {
        return switch (type) {
            case WARN -> "Warning";
            case KICK -> "Kick";
            case MUTE -> "Mute";
            case BAN -> "Ban";
        };
    }
    
    private String formatStatus() {
        if (type == PunishmentType.KICK) {
            return null;
        }
        
        if (active) {
            return untilTime == null ? "Active, does not expire" : "Active, expires " + TimeFormat.RELATIVE.format(untilTime.getTime());
        }
        
        if (removeDate != null && removeByName != null && !removeByName.equals("#expired")) {
            return "Removed by staff " + TimeFormat.RELATIVE.format(removeDate.getTime());
        }
        
        Timestamp ended = removeDate != null ? removeDate : untilTime;
        return ended == null ? "Expired" : "Expired " + TimeFormat.RELATIVE.format(ended.getTime());
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

