package com.diamondfire.helpbot;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import javax.security.auth.login.LoginException;

public class HelpBot {
    
    public static void main(String[] args) throws LoginException {
        CodeDatabase.initialize();
        HelpBotInstance.initialize();
        CodeDifferenceHandler.refresh();
    }
    
}
