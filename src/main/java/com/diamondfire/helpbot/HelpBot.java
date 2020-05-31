package com.diamondfire.helpbot;

import com.diamondfire.helpbot.components.externalfile.ExternalFile;
import com.diamondfire.helpbot.instance.InstanceHandler;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HelpBot {

    public static InstanceHandler instance = new InstanceHandler();

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        instance.startup();


    }
}
