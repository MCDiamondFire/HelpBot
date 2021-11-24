package com.diamondfire.helpbot.bot.command.impl.other.util;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AbstractFileListCommand extends Command {
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    protected void generate(CommandEvent event, List<? extends CodeObject> data) {
        List<String> names = new ArrayList<>();
        for (CodeObject item : data) {
            names.add(item.getItem().getItemName());
        }
        
        event.getReplyHandler().replyFile(new EmbedBuilder().setTitle("File Generated"), String.join("|", names).getBytes(StandardCharsets.UTF_8),
                data.get(0).getEnum().getName().toLowerCase().replace(" ", "-") + "-list.txt");
    }
}
