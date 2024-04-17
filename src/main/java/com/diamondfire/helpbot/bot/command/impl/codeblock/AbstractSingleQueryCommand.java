package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MessageArgument;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.df.codeinfo.viewables.BasicReaction;
import com.diamondfire.helpbot.sys.interaction.button.ButtonHandler;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public abstract class AbstractSingleQueryCommand extends Command {
    
    public static void sendMultipleMessage(List<CodeObject> actions, TextChannel channel, long userToWait, BiConsumer<CodeObject, TextChannel> onChosen) {
        // This here is to determine if all the duplicate types are the same. If not, we need to make sure that we filter those out first..
        CodeObject referenceData = actions.get(0);
        Class<? extends CodeObject> classReference = referenceData.getClass();
        PresetBuilder preset = new PresetBuilder();
        preset.withPreset(
                new InformativeReply(InformativeReplyType.INFO, "Duplicate Objects Detected!",
                        "What you are searching for contains a duplicate entry. Let's narrow down your search.")
        );
        
        boolean isSameType = true;
        for (CodeObject data : actions) {
            if (!data.getClass().isAssignableFrom(classReference)) {
                isSameType = false;
                break;
            }
        }
        //If they are the same type, use the dupe emojis for that certain action type.
        Map<String, CodeObject> buttonMap = new HashMap<>();
        List<Button> buttons = new ArrayList<>();
        if (isSameType) {
            for (Map.Entry<BasicReaction, CodeObject> reaction : referenceData.getEnum().getEmbedBuilder().generateDupeEmojis(actions).entrySet()) {
                Button button = Button.secondary(reaction.getKey().toString(), reaction.getValue().getName());
                
                buttons.add(button.withEmoji(Emoji.fromCustom(reaction.getKey().getEmote())));
                buttonMap.put(button.getId(), reaction.getValue());
            }
        } else {
            for (CodeObject data : actions) {
                long emoji = data.getEnum().getEmoji();
                Button button = Button.secondary(String.valueOf(data.getEnum().getEmoji()), data.getName());
                
                buttons.add(button.withEmoji(HelpBotInstance.getJda().getEmojiById(emoji)));
                buttonMap.put(button.getId(), data);
            }
        }
        
        channel.sendMessageEmbeds(preset.getEmbed().build()).setActionRow(buttons).queue((message) -> {
            ButtonHandler.addListener(userToWait, message, (event) -> {
                message.delete().queue();
                
                // when msg is deleted causes nullpointer when tries to remove reactions! FIX
                CodeObject object = buttonMap.get(event.getComponentId());
                onChosen.accept(object, message.getChannel().asTextChannel());
            });
        });
        
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("name",
                        new MessageArgument());
    }
    
    @Override
    public void run(CommandEvent event) {
        getData(event, onDataReceived());
    }
    
    public abstract BiConsumer<CodeObject, TextChannel> onDataReceived();
    
    protected void getData(CommandEvent event, BiConsumer<CodeObject, TextChannel> onChosen) {
        String name = event.getArgument("name");
        PresetBuilder preset = new PresetBuilder();
        
        //Generate a bunch of "favorable" actions.
        Map<CodeObject, Double> possibleChoices = new HashMap<>();
        for (CodeObject data : CodeDatabase.getStandardObjects()) {
            double nameScore = JaroWinkler.score(name, data.getName());
            double iconNameScore = JaroWinkler.score(name, data.getItem().getItemName());
            if (nameScore >= 0.8 || iconNameScore >= 0.8) {
                possibleChoices.put(data, Math.max(nameScore, iconNameScore));
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
                    onChosen.accept(sameActions.get(0), event.getChannel().asTextChannel());
                } else if (sameActions.size() > 1) {
                    sendMultipleMessage(sameActions, event.getChannel().asTextChannel(), event.getMember().getIdLong(), onChosen);
                }
                
                return;
                // Either there are too many similar actions or there is no close action.
            } else {
                preset.withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("I couldn't exactly find `%s`! \nHere are some similar objects", StringUtil.display(EmbedUtil.titleSafe(name))))
                );
                List<String> similarActionNames = possibleChoices.entrySet().stream()
                        .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                        .map((entry) -> entry.getKey().getName())
                        .collect(Collectors.toList());
                Collections.reverse(similarActionNames);
                
                EmbedUtil.addFields(preset.getEmbed(), similarActionNames);
            }
            
        } else {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, String.format("Couldn't find anything that matched `%s`!", StringUtil.display(EmbedUtil.titleSafe(name))))
            );
        }
        event.reply(preset);
    }
    
    
}
