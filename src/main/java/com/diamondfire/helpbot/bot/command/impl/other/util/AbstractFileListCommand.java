package com.diamondfire.helpbot.bot.command.impl.other.util;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.*;
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
        File file;
        try {
            file = ExternalFileUtil.generateFile(data.get(0).getEnum().getName().toLowerCase().replace(" ", "-") + "-list.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath(), true))) {
                List<String> names = new ArrayList<>();
                for (CodeObject item : data) {
                    names.add(item.getItem().getItemName());
                }
                
                writer.append(String.join("|", names));
            }
            
        } catch (IOException e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Could not generate file!");
            event.getChannel().sendMessageEmbeds(builder.build()).queue();
            return;
        }
        
        event.getChannel().sendMessage("File Generated").addFiles(FileUpload.fromData(file)).queue();
    }
}
