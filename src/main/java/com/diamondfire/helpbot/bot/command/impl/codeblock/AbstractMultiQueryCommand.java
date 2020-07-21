package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.MessageArgument;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.*;


public abstract class AbstractMultiQueryCommand extends Command {

    protected abstract List<String> filterData(List<SimpleData> data, CommandEvent event);

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("name", new MessageArgument());
    }


    //TODO Cleaner implementation. NOW
    @Override
    public void run(CommandEvent event) {
        List<String> names = filterData(CodeDatabase.getSimpleData(), event);
        Collections.sort(names);
        EmbedBuilder builder = new EmbedBuilder();

        if (names.size() != 0) {
            Util.addFields(builder, names);

            if (builder.getFields().size() >= 5) {
                builder.setTitle("This search yields too many results! Please narrow down your search.");
                builder.clearFields();
                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            builder.setTitle(String.format("Search results for `%s`!", getSearchQuery(event)));

            // If possible choices is empty, meaning none can be found.
        } else {
            builder.setTitle(String.format("I couldn't find anything that matched `%s`!", getSearchQuery(event)));
        }
        builder.setFooter(String.format("%s %s found", names.size(), Util.sCheck("object", names.size())));
        event.getChannel().sendMessage(builder.build()).queue();

    }

    protected String getSearchQuery(CommandEvent event) {
        return MarkdownSanitizer.sanitize(event.getArgument("name"), MarkdownSanitizer.SanitizationStrategy.ESCAPE);
    }

}
