package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MultiArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.bot.reactions.impl.ReactionHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.df.codeinfo.viewables.BasicReaction;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public abstract class AbstractSingleQueryCommand extends Command {

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("name", new MultiArgumentContainer<>(new StringArgument()));
    }

    @Override
    public void run(CommandEvent event) {
        getData(event, onDataReceived());
    }

    public abstract BiConsumer<CodeObject, TextChannel> onDataReceived();


    public static void sendMultipleMessage(List<CodeObject> actions, TextChannel channel, long userToWait, BiConsumer<CodeObject, TextChannel> onChosen) {
        // This here is to determine if all the duplicate types are the same. If not, we need to make sure that we filter those out first..
        CodeObject referenceData = actions.get(0);
        Class<? extends CodeObject> classReference = referenceData.getClass();
        Map<BasicReaction, CodeObject> emojis = new HashMap<>();
        PresetBuilder preset = new PresetBuilder();
        preset.withPreset(
                new InformativeReply(InformativeReplyType.INFO, "Duplicate Objects Detected!",
                        "What you are searching for contains a duplicate entry, please react accordingly so I can figure out what you are looking for.")
        );

        boolean isSameType = true;
        for (CodeObject data : actions) {
            if (!data.getClass().isAssignableFrom(classReference)) {
                isSameType = false;
                break;
            }
        }
        //If they are the same type, use the dupe emojis for that certain action type.
        if (isSameType) {
            emojis.putAll(referenceData.getEnum().getEmbedBuilder().generateDupeEmojis(actions));
        } else {
            for (CodeObject data : actions) {
                emojis.put(new BasicReaction(data.getEnum().getEmoji()), data);
            }
        }

        List<String> options = new ArrayList<>();
        for (Map.Entry<BasicReaction, CodeObject> emoji : emojis.entrySet()) {
            options.add(emoji.getKey().toString() + " - " + emoji.getValue().getName());
        }
        preset.getEmbed().addField("**Options**", StringUtil.listView("", options), false);

        channel.sendMessage(preset.getEmbed().build()).queue((message) -> {
            ReactionHandler.waitReaction(userToWait, message, (event) -> {
                message.delete().queue();

                // when msg is deleted causes nullpointer when tries to remove reactions! FIX
                List<CodeObject> filteredData = new ArrayList<>();
                for (Map.Entry<BasicReaction, CodeObject> emoji : emojis.entrySet()) {
                    if (emoji.getKey().equalToReaction(event.getReactionEvent().getReactionEmote())) {
                        filteredData.add(emoji.getValue());
                    }
                }

                if (filteredData.size() == 1) {
                    onChosen.accept(filteredData.get(0), message.getTextChannel());
                } else {
                    sendMultipleMessage(filteredData, message.getTextChannel(), userToWait, onChosen);
                }

            });
            for (BasicReaction reaction : emojis.keySet()) {
                reaction.react(message).queue();
            }
        });

    }

    protected void getData(CommandEvent event, BiConsumer<CodeObject, TextChannel> onChosen) {
        List<String> args = event.getArgument("name");
        String argumentsParsed = String.join(" ", args);
        PresetBuilder preset = new PresetBuilder();

        //Generate a bunch of "favorable" actions.
        Map<CodeObject, Double> possibleChoices = new HashMap<>();
        for (CodeObject data : CodeDatabase.getStandardObjects()) {
            double nameScore = JaroWinkler.score(argumentsParsed, data.getName());
            double iconNameScore = JaroWinkler.score(argumentsParsed, data.getItem().getItemName());
            if (nameScore >= 0.8 || iconNameScore >= 0.8) {
                possibleChoices.put(data, Math.max(nameScore,iconNameScore));
            }
        }

        //Get the most similar action possible.
        Map.Entry<CodeObject, Double> closestAction = possibleChoices.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElse(null);

        // (Prevents random words from being picked when there is a wide variety of close choices too)
        if (closestAction != null) {
            // If it isn't accurate enough
            if (closestAction.getValue() >= 0.90) {
                // Find actions that are exactly the same
                List<CodeObject> sameActions = new ArrayList<>();
                for (CodeObject data : possibleChoices.keySet()) {
                    if (data.getName().equalsIgnoreCase(closestAction.getKey().getName())) {
                        sameActions.add(data);
                    }
                }

                // If none, proceed. Else we need to special case that.
                if (sameActions.size() == 1) {
                    onChosen.accept(sameActions.get(0), event.getChannel());
                } else if (sameActions.size() > 1) {
                    sendMultipleMessage(sameActions, event.getChannel(), event.getMember().getIdLong(), onChosen);
                }

                return;
                // Either there are too many similar actions or there is no close action.
            } else {
                preset.withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("I couldn't exactly find `%s`! \nHere are some similar objects", StringUtil.display(StringUtil.titleSafe(argumentsParsed))))
                );
                List<String> similarActionNames = possibleChoices.entrySet().stream()
                        .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                        .map((entry) -> entry.getKey().getName())
                        .collect(Collectors.toList());
                Collections.reverse(similarActionNames);

                Util.addFields(preset.getEmbed(), similarActionNames);
            }

        } else {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, String.format("Couldn't find anything that matched `%s`!", StringUtil.display(StringUtil.titleSafe(argumentsParsed))))
            );
        }
        event.reply(preset);
    }


}
