package com.owen1212055.helpbot.instance;

import com.owen1212055.helpbot.components.codedatabase.AutoRefreshDBTask;
import com.owen1212055.helpbot.components.codedatabase.CodeDifferenceHandler;
import com.owen1212055.helpbot.components.codedatabase.db.CodeDatabase;
import com.owen1212055.helpbot.components.ExternalFileHandler;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InstanceHandler {

    public void startup() throws LoginException, InterruptedException, IOException {

        ExternalFileHandler.initialize();
        BotInstance.start();
        CodeDatabase.initialize();
        CodeDifferenceHandler.refresh();
        AutoRefreshDBTask.initialize();


    }
}
