package com.diamondfire.helpbot.sys.message.filter;


import net.dv8tion.jda.api.entities.Message;

public final class ChatFilters {
    
    private static final ChatFilter[] chatFilters = new ChatFilter[]{
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
