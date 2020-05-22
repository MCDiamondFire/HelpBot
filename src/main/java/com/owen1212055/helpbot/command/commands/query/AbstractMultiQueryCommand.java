package com.owen1212055.helpbot.command.commands.query;

import com.owen1212055.helpbot.command.commands.Command;
import com.owen1212055.helpbot.components.codedatabase.db.CodeDatabase;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.BasicStringArg;
import com.owen1212055.helpbot.events.CommandEvent;
import com.owen1212055.helpbot.util.StringFormatting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractMultiQueryCommand extends Command {

    protected abstract List<String> filterData(List<SimpleData> data, CommandEvent event);

    @Override
    public Argument getArgument() {
        return new BasicStringArg();
    }

    @Override
    public void run(CommandEvent event) {

        List<String> names = filterData(CodeDatabase.getSimpleData(), event);
        Collections.sort(names);
        EmbedBuilder builder = new EmbedBuilder();
        if (names.size() != 0) {

            String list = null;
            String lastList = null;
            LinkedList<String> queue = new LinkedList<>();

            for (int i = 0; i < names.size(); i++) {
                String dataName = names.get(i);
                queue.add(dataName);
                list = StringFormatting.listView(queue.toArray(new String[0]), "> ", true);

                if (i == names.size() - 1) {
                    builder.addField("<:air:309522480391913474>", list, false);
                } else if (list.length() >= 1000) {
                    queue.removeFirst();
                    builder.addField("<:air:309522480391913474>", lastList, false);
                    queue.clear();
                    queue.add(dataName);
                }
                lastList = list;

            }
            if (builder.getFields().size() >= 5) {
                builder.setTitle("This search yields too many results! Please narrow down your search.");
                builder.clearFields();

                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            builder.setTitle(String.format("Search results for `%s`!", getSearchQuery(event)));

        }// If possible choices is empty, meaning none can be found.
        else {
            builder.setTitle(String.format("I couldn't find anything that matched `%s`!", getSearchQuery(event)));
        }
        event.getChannel().sendMessage(builder.build()).queue();

    }
    protected String getSearchQuery(CommandEvent event) {
        return MarkdownSanitizer.sanitize(event.getParsedArgs(), MarkdownSanitizer.SanitizationStrategy.REMOVE);
    }

}
