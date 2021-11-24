package com.diamondfire.helpbot.sys.graph.generators;

import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.generators.context.TimeGraphContext;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import org.intellij.lang.annotations.Language;

import java.sql.ResultSet;
import java.util.*;

public class QueryGraphGenerator implements GraphGenerator<TimeGraphContext> {
    
    private final String title;
    @Language("SQL")
    private final String query;
    
    public QueryGraphGenerator(String title, @Language("SQL") String query) {
        this.title = title;
        this.query = query;
    }
    
    @Override
    public byte[] createGraph(TimeGraphContext context) {
        Map<GraphableEntry<?>, Integer> entries = new LinkedHashMap<>();
        ChartGraphBuilder builder = new ChartGraphBuilder();
        TimeMode timeMode = context.getMode();
        
        new DatabaseQuery()
                .query(new BasicQuery(query, statement -> {
                    statement.setString(1, timeMode.getDateFormat());
                    statement.setInt(2, timeMode.getUnitConversion().apply(context.getNum()));
                }))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        entries.put(new StringEntry(set.getString("time")), set.getInt("count"));
                    }
                
                    builder.setGraphName(timeMode.getMode() + " " + title);
                });
        
        return builder.createGraph(entries);
    }
}
