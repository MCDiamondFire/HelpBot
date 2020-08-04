package com.diamondfire.helpbot.df.punishments.fetcher.providers;

import com.diamondfire.helpbot.df.punishments.*;

import java.sql.*;

public abstract class StaticPunishmentProvider implements PunishmentProvider {

    @Override
    public Punishment getPunishment(ResultSet data) throws SQLException {
        return new Punishment(getType(), data.getString("uuid"), data.getString("reason"),
                data.getString("banned_by_uuid"), data.getString("banned_by_name"),
                data.getDate("time"), data.getDate("until"),
                data.getBoolean("silent"), data.getBoolean("active"),
                null,null,null);
    }

    @Override
    public String getQuery() {
        return "SELECT uuid, reason, banned_by_uuid, banned_by_name, FROM_UNIXTIME(time / 1000) AS time, FROM_UNIXTIME(until / 1000) AS until, silent, active " +
                "FROM litebans." + getDBTable() + " WHERE uuid = ? ORDER BY time DESC;";
    }
}
