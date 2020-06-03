package com.diamondfire.helpbot.command.impl.query;

import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.components.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringFormatting;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractMultiQueryCommand extends Command {

    protected abstract List<String> filterData(List<SimpleData> data, CommandEvent event);

    @Override
    public ValueArgument<String> getArgument() {
        return new StringArg("Name", true);
    }


    //TODO Cleaner implementation. NOW
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
                    builder.addField("", list, false);
                } else if (list.length() >= 1000) {
                    queue.removeFirst();
                    builder.addField("", lastList, false);

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
        builder.setFooter(String.format("%s %s found", names.size(), Util.sCheck("object", names.size())));
        event.getChannel().sendMessage(builder.build()).queue();

    }

    protected String getSearchQuery(CommandEvent event) {
        return MarkdownSanitizer.sanitize(event.getParsedArgs(), MarkdownSanitizer.SanitizationStrategy.REMOVE);
    }

}
