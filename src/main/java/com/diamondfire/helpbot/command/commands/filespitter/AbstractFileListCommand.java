package com.diamondfire.helpbot.command.commands.filespitter;

import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.commands.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.ExternalFileHandler;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.command.arguments.Argument;
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
