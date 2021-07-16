package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.command.impl.other.util.FetchDataCommand;
import com.diamondfire.helpbot.sys.tasks.MidnightTask;

public class CodeDatabaseTask implements MidnightTask {
    
    @Override
    public void run() {
        new FetchDataCommand().setup(RESPONSE_CHANNEL);
    }
}
