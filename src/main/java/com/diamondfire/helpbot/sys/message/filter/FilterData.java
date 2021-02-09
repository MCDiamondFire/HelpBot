package com.diamondfire.helpbot.sys.message.filter;


import net.dv8tion.jda.api.entities.Message;

public class FilterData {
    
    private final Message message;
    private final String simplifiedMessage;
    
    public FilterData(Message message) {
        this.message = message;
        this.simplifiedMessage = simplifyString(message.getContentDisplay());
    }
    
    public Message getMessage() {
        return message;
    }
    
    public String getSimplifiedMessage() {
        return simplifiedMessage;
    }
    
    private static String simplifyString(String string) {
        string = string.toLowerCase();
        string = string.replace("'", "");
        string = string.replaceAll(" +", " ").trim();
        
        return string;
    }
}
