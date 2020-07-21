package com.diamondfire.helpbot.bot.command.impl.filespitter;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.*;
import java.util.List;

public abstract class AbstractFileListCommand extends Command {

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    protected void generate(CommandEvent event, List<? extends SimpleData> data) {

        File file;
        try {
            file = ExternalFileUtil.generateFile(data.get(0).getEnum().getName().toLowerCase().replace(" ", "-") + "-list.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath(), true))) {

                String[] names = data.stream()
                        .map(simpleData -> simpleData.getItem().getItemName())
                        .toArray(String[]::new);

                writer.append(String.join("|", names));
            }

        } catch (IOException e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Could not generate file!");
            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }

        event.getChannel().sendMessage("File Generated...").addFile(file).queue();


    }
}
