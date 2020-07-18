package com.diamondfire.helpbot.command.impl.codeblock;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.argument.impl.types.MessageArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.components.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.components.reactions.impl.ReactionHandler;
import com.diamondfire.helpbot.components.viewables.BasicReaction;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public abstract class AbstractSingleQueryCommand extends Command {

    public static void sendMultipleMessage(List<SimpleData> actions, TextChannel channel, long userToWait, BiConsumer<SimpleData, TextChannel> onChosen) {
        // This here is to determine if all the duplicate types are the same. If not, we need to make sure that we filter those out first..
        Class<? extends SimpleData> classReference = actions.get(0).getClass();
        HashMap<BasicReaction, SimpleData> emojis = new HashMap<>();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Duplicate actions detected!");
        builder.setDescription("What you are searching for contains a duplicate entry, please react accordingly so I can figure out what you are looking for.");

        if (actions.stream().allMatch((simpleData -> simpleData.getClass().isAssignableFrom(classReference)))) {
            // If here all of the types are the same
            emojis = actions.get(0).getEnum().getEmbedBuilder().generateDupeEmojis(actions);
        } else {
            // We have special types, we need to filter those out.
            for (SimpleData data : actions) {
                emojis.put(new BasicReaction(data.getEnum().getEmoji()), data);
            }
        }
        builder.addField("Options:", emojis.entrySet().stream()
                        .map((dataEntry -> "\n" + dataEntry.getKey().toString() + " - " + dataEntry.getValue().getMainName()))
                        .collect(Collectors.joining()),
                false);


        HashMap<BasicReaction, SimpleData> finalEmojis = emojis;
        channel.sendMessage((builder.build())).queue((message) -> {
            ReactionHandler.waitReaction(userToWait, message, (event -> {
                message.delete().queue();

                // when msg is deleted causes nullpointer when tries to remove reactions! FIX
                ArrayList<SimpleData> filteredData = finalEmojis.entrySet().stream()
                        .filter((entry) -> entry.getKey().equalToReaction(event.getReactionEvent().getReactionEmote()))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toCollection(ArrayList::new));

                if (filteredData.size() == 1) {
                    if (filteredData.get(0) == null) return;
                    onChosen.accept(filteredData.get(0), message.getTextChannel());
                } else {
                    sendMultipleMessage(filteredData, message.getTextChannel(), userToWait, onChosen);

                }

            }));
            finalEmojis.keySet().forEach((emote -> emote.react(message).queue()));
        });

    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("name", new MessageArgument());
    }

    @Override
    public void run(CommandEvent event) {
        getData(event, onDataReceived());
    }

    public abstract BiConsumer<SimpleData, TextChannel> onDataReceived();

    protected void getData(CommandEvent event, BiConsumer<SimpleData, TextChannel> onChosen) {
        String argumentsParsed = event.getArgument("name");

        //Generate a bunch of "favorable" actions.
        Map<SimpleData, Double> possibleChoices = new HashMap<>();
        for (SimpleData data : CodeDatabase.getSimpleData()) {
            double compScore = JaroWinkler.score(argumentsParsed, data.getMainName());
            if (compScore >= 0.85) {
                possibleChoices.put(data, compScore);
            }
        }

        //Get the most similar action possible.
        Map.Entry<SimpleData, Double> closestAction = possibleChoices.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElse(null);

        // (Prevents random words from being picked when there is a wide variety of close choices too)
        if (closestAction != null) {
            if (possibleChoices.size() < 10 || closestAction.getKey().getMainName().toLowerCase().equals(argumentsParsed.toLowerCase())) {
                // Find actions that are exactly the same
                List<SimpleData> sameActions = possibleChoices.keySet().stream()
                        .filter(data -> data.getMainName().equals(closestAction.getKey().getMainName()))
                        .collect(Collectors.toList());

                // If none, proceed. Else we need to special case that.
                if (sameActions.size() == 1) {
                    onChosen.accept(sameActions.get(0), event.getChannel());
                } else if (sameActions.size() > 1) {
                    try {
                        sendMultipleMessage(sameActions, event.getChannel(), event.getMember().getIdLong(), onChosen);
                    } catch (Exception e) {
                        Util.error(e, "Error while sending multi-reaction msg!");
                        e.printStackTrace();
                    }
                }

                // Either there are too many similar actions or there is no close action.
            } else {

                EmbedBuilder builder = new EmbedBuilder();
                Collection<String> similarActionNames = possibleChoices.keySet().stream()
                        .map(SimpleData::getMainName)
                        .collect(Collectors.toCollection(ArrayList::new));

                builder.setDescription("\\> " + String.join("\n \\> ", similarActionNames));
                builder.setTitle(String.format("Too many actions were too similar to `%s`\nhere are some similar actions.", MarkdownSanitizer.sanitize(StringUtil.titleSafe(argumentsParsed), MarkdownSanitizer.SanitizationStrategy.ESCAPE)));


                event.getChannel().sendMessage(builder.build()).queue();
            }

            // If possible choices is empty, meaning none can be found.
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(String.format("I couldn't find anything that matched `%s`!", MarkdownSanitizer.sanitize(StringUtil.titleSafe(argumentsParsed), MarkdownSanitizer.SanitizationStrategy.ESCAPE)));
            event.getChannel().sendMessage(builder.build()).queue();

        }
    }

}
