package com.diamondfire.helpbot;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.AutoRefreshDBTask;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.restart.RestartHandler;

import javax.security.auth.login.LoginException;

public class HelpBot {

    public static void main(String[] args) throws LoginException, InterruptedException {
        HelpBotInstance.initialize();
        CodeDatabase.initialize();
        CodeDifferenceHandler.refresh();
        AutoRefreshDBTask.initialize();
        RestartHandler.recover();
    }
}
