package com.diamondfire.helpbot;


import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.sys.tag.TagHandler;
import com.diamondfire.helpbot.util.textgen.SamQuotes;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class HelpBot {
    
    public static void main(String[] args) throws LoginException {
        CodeDatabase.initialize();
        HelpBotInstance.initialize();
        CodeDifferenceHandler.refresh();
    
        try {
            SamQuotes.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
