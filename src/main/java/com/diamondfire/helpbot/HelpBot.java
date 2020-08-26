package com.diamondfire.helpbot;


import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.bot.command.disable.DisableCommandHandler;

import javax.security.auth.login.LoginException;

public class HelpBot {

    public static void main(String[] args) throws LoginException {
        HelpBotInstance.initialize();
        CodeDatabase.initialize();
        CodeDifferenceHandler.refresh();
        DisableCommandHandler.initialize();
    }

}
