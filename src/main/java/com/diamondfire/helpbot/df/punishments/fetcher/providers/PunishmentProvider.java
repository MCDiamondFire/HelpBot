package com.diamondfire.helpbot.df.punishments.fetcher.providers;

import com.diamondfire.helpbot.df.punishments.*;
import org.intellij.lang.annotations.Language;

import java.sql.*;

public interface PunishmentProvider {
    
    Punishment getPunishment(ResultSet data) throws SQLException;
    
    @Language("SQL")
    String getQuery();
    
    PunishmentType getType();
    
    String getDBTable();
}
