package com.diamondfire.helpbot.df.filter;


import com.diamondfire.helpbot.df.filter.filters.SwearFilter;
import net.dv8tion.jda.api.entities.Message;

public final class ChatFilters {
    
    private static final ChatFilter[] chatFilters = new ChatFilter[]{
            new SwearFilter()
    };
    
    public static boolean filterMessage(Message message) {
        FilterData filterData = new FilterData(message);
        
        for (ChatFilter filter : chatFilters) {
            boolean filterResult = filter.filter(filterData);
            if (filterResult) {
                message.delete().queue();
                return false;
            }
        }
        
        return true;
    }
    
}
