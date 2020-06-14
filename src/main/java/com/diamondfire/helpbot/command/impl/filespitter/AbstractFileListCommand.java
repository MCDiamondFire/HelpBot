package com.diamondfire.helpbot.command.impl.filespitter;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.components.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class AbstractFileListCommand extends Command {


    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }

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
