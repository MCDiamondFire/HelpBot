package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.components.externalfile.ExternalFile;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SamQuotesCommand extends Command {

    @Override
    public String getName() {
        return "samquote";
    }

    @Override
    public String getDescription() {
        return "Gets a quote from Sam the Man";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.STATS;
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        String[] strings = ExternalFile.SAM_DIR.getFile().list();
        File file = new File(ExternalFile.SAM_DIR.getFile(), strings[new Random().nextInt(strings.length)]);
        event.getChannel().sendFile(file).queue();

    }


}


