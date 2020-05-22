package com.owen1212055.helpbot.command.commands.filespitter;

import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.NoArg;
import com.owen1212055.helpbot.command.commands.Command;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.components.ExternalFileHandler;
import com.owen1212055.helpbot.components.codedatabase.db.CodeDatabase;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class AbstractFileListCommand extends Command {

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }


    protected void generate(CommandEvent event, List<? extends SimpleData> data) {

        File file;
        try {

            file = ExternalFileHandler.generateFile(data.get(0).getEnum().getName().toLowerCase().replace(" ", "_") + "-list.txt");

            BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath(), true));

            StringBuilder stringBuilder = new StringBuilder();

            data.forEach((simpleData -> stringBuilder.append(simpleData.getItem().getItemName() + "|")));

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            writer.append(stringBuilder.toString());
            writer.close();
        } catch (IOException e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Could not generate file!");
            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }

        event.getChannel().sendMessage("Here you go!").addFile(file).queue();



    }
}
