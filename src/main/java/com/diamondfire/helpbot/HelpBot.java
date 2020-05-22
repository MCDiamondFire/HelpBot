package com.diamondfire.helpbot;

import com.diamondfire.helpbot.instance.InstanceHandler;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class HelpBot {
    public static InstanceHandler instance = new InstanceHandler();

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        instance.startup();
    }
}
