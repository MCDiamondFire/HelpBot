package com.diamondfire.helpbot.sys.message.acceptors;

import com.diamondfire.helpbot.sys.message.filter.ChatFilters;
import net.dv8tion.jda.api.entities.Message;

public class FilterAcceptor implements MessageAcceptor {
    
    @Override
    public boolean accept(Message message) {
        return ChatFilters.filterMessage(message);
    }
}
