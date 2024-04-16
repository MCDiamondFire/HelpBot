package com.diamondfire.helpbot;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.util.StarUtil;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;

public class HelpBot {
    
    public static void main(String[] args) throws LoginException {
        try {
            File file = new File("test/test.png");
            file.mkdirs();
            ImageIO.write(StarUtil.create(Color.CYAN), "png", new File("test/test.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (true) return;
        CodeDatabase.initialize();
        HelpBotInstance.initialize();
        CodeDifferenceHandler.refresh();
    }
    
}
