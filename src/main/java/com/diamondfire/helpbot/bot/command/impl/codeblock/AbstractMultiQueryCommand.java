package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MultiArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.*;


public abstract class AbstractMultiQueryCommand extends Command {

    protected abstract List<String> filterData(List<CodeObject> data, CommandEvent event);

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("name", new MultiArgumentContainer<>(new StringArgument()));
    }

    @Override
    public void run(CommandEvent event) {
        List<String> names = filterData(CodeDatabase.getStandardObjects(), event);
        Collections.sort(names);
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder builder = preset.getEmbed();

        if (names.size() != 0) {
            Util.addFields(preset.getEmbed(), names);

            if (builder.getFields().size() >= 5) {
                builder.clear();
                preset.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "This search yields too many results! Please narrow down your search.")
                );
                event.reply(preset);
                return;
            }

            builder.setTitle(String.format("Search results for `%s`!", getSearchQuery(event)));
            // If possible choices is empty, meaning none can be found.
        } else {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, String.format("I couldn't find anything that matched `%s`!", getSearchQuery(event)))
            );
        }
        builder.setFooter(String.format("%s %s found", names.size(), Util.sCheck("object", names.size())));

        event.reply(preset);
    }

    protected String getSearchQuery(CommandEvent event) {
        List<String> strings = event.getArgument("name");
        return MarkdownSanitizer.sanitize(String.join(" ", strings), MarkdownSanitizer.SanitizationStrategy.ESCAPE);
    }

}
