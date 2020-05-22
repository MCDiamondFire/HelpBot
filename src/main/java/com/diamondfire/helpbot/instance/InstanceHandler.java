package com.diamondfire.helpbot.instance;

import com.diamondfire.helpbot.components.ExternalFileHandler;
import com.diamondfire.helpbot.components.codedatabase.AutoRefreshDBTask;
import com.diamondfire.helpbot.components.codedatabase.CodeDifferenceHandler;
import com.diamondfire.helpbot.components.codedatabase.db.CodeDatabase;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class InstanceHandler {

    public void startup() throws LoginException, InterruptedException, IOException {

        ExternalFileHandler.initialize();
        BotInstance.start();
        CodeDatabase.initialize();
        CodeDifferenceHandler.refresh();
        AutoRefreshDBTask.initialize();


    }
}
