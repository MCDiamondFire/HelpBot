package com.diamondfire.helpbot.df.punishments.fetcher.providers;

import com.diamondfire.helpbot.df.punishments.Punishment;

import java.sql.*;

public abstract class RemovablePunishmentProvider implements PunishmentProvider {

    @Override
    public Punishment getPunishment(ResultSet data) throws SQLException {
        return new Punishment(getType(), data.getString("uuid"), data.getString("reason"),
                data.getString("banned_by_uuid"), data.getString("banned_by_name"),
                data.getDate("time"), data.getDate("until"), data.getBoolean("silent"),
                data.getBoolean("active"), data.getString("removed_by_uuid"),
                data.getString("removed_by_name"), data.getTimestamp("removed_by_date"));
    }


    @Override
    public String getQuery() {
        return "SELECT uuid, reason, banned_by_uuid, banned_by_name, FROM_UNIXTIME(time / 1000) AS time, FROM_UNIXTIME(until / 1000) AS until, silent, active, " +
                "removed_by_uuid, removed_by_name, removed_by_date " +
                "FROM litebans." + getDBTable() + " WHERE uuid = ? ORDER BY time DESC;";
    }

}
