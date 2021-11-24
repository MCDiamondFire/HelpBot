package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.util.Random;

public class GetSamquoteSubCommand extends SubCommand {
    
    private static final Random random = new Random();
    
    @Override
    public String getName() {
        return "get";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext();
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        runStatic(event);
    }
    
    public static void runStatic(CommandEvent event) {
        String[] strings = ExternalFiles.SAM_DIR.list();
        File file = new File(ExternalFiles.SAM_DIR, strings[random.nextInt(strings.length)]);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Sam Quote");
        builder.setImage("attachment://quote.png");
        builder.setColor(new Color(87, 177, 71));
    
        event.getReplyHandler().replyFile(builder, file, "quote.png");
    }
}
