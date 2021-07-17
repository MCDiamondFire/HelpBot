package com.diamondfire.helpbot;


import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.sys.tag.TagHandler;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class HelpBot {
    
    public static void main(String[] args) throws LoginException, IOException {
        CodeDatabase.initialize();
        TagHandler.cacheJson();
        HelpBotInstance.initialize();
        CodeDifferenceHandler.refresh();
    }
    
}
