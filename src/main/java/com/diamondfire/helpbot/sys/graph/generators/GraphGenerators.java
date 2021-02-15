package com.diamondfire.helpbot.sys.graph.generators;

public interface GraphGenerators {
    
    QueryGraphGenerator UNIQUE_JOINS = new QueryGraphGenerator("unique player joins", "" +
            "SELECT time, COUNT(*) AS count " +
            "FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, ?) AS time" +
            "      FROM hypercube.player_join_log" +
            "      WHERE time > ((NOW() - INTERVAL ? HOUR) - INTERVAL MINUTE(NOW()) MINUTE) - INTERVAL SECOND(NOW()) SECOND" +
            "        AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
            "GROUP BY time");
    
    QueryGraphGenerator NEW_PLAYERS = new QueryGraphGenerator("new player joins", "" +
            "SELECT time, COUNT(*) AS count " +
            "FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, ?) AS time" +
            "      FROM hypercube.approved_users" +
            "      WHERE time > ((NOW() - INTERVAL ? HOUR) - INTERVAL MINUTE(NOW()) MINUTE) - INTERVAL SECOND(NOW()) SECOND" +
            "        AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
            "GROUP BY time");
    
}
