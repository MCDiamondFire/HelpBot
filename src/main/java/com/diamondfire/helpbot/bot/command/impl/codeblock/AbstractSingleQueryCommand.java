package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MessageArgument;
import com.diamondfire.helpbot.bot.command.argument.impl.types.SuggestingArgument;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.sys.interaction.select.SelectMenuHandler;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.messages.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;
import java.util.stream.Collectors;


public abstract class AbstractSingleQueryCommand extends Command {
    
    public static void sendMultipleMessage(List<CodeObject> actions, CommandEvent event, BiConsumer<CodeObject, Consumer<MessageCreateData>> onChosen) {
        sendSelectMessage(actions, "Duplicate Objects Detected!", "What you are searching for contains a duplicate entry. Let's narrow down your search.", event, onChosen);
    }
    
    public static void sendSelectMessage(List<CodeObject> objects, String title, String description, CommandEvent event, BiConsumer<CodeObject, Consumer<MessageCreateData>> onChosen) {
        List<CodeObject> options = objects.subList(0, Math.min(objects.size(), SelectMenu.OPTIONS_MAX_AMOUNT));
        StringSelectMenu.Builder menu = StringSelectMenu.create("code_object").setPlaceholder("Choose an object");
        for (int i = 0; i < options.size(); i++) {
            CodeObject data = options.get(i);
            String type = data instanceof ActionData action ? data.getEnum().getName() + " | " + action.getCodeblockName() : data.getEnum().getName();
            SelectOption option = SelectOption.of(data.getName(), String.valueOf(i)).withDescription(type);
            
            Emoji emoji = HelpBotInstance.getJda().getEmojiById(data.getEnum().getEmoji());
            menu.addOptions(emoji == null ? option : option.withEmoji(emoji));
        }
        
        PresetBuilder preset = new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.INFO, title, description));
        MessageCreateData prompt = new MessageCreateBuilder()
                .setEmbeds(preset.getEmbed().build())
                .setComponents(ActionRow.of(menu.build()))
                .build();
        
        event.reply(prompt, (message) -> SelectMenuHandler.addListener(event.getAuthor().getIdLong(), message, (selectEvent) -> {
            AtomicBoolean edited = new AtomicBoolean();
            CodeObject object = options.get(Integer.parseInt(selectEvent.getValues().get(0)));
            onChosen.accept(object, (result) -> {
                if (edited.compareAndSet(false, true)) {
                    selectEvent.editMessage(MessageEditData.fromCreateData(result)).setReplace(true).queue();
                } else {
                    selectEvent.getChannel().sendMessage(result).queue();
                }
            });
        }));
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("name",
                        new SuggestingArgument<>(new MessageArgument(), (input) -> CodeDatabase.getStandardObjects().stream()
                                .map(CodeObject::getName)
                                .sorted()
                                .toList()));
    }
    
    
    @Override
    public void run(CommandEvent event) {
        getData(event, onDataReceived());
    }
    
    public abstract BiConsumer<CodeObject, Consumer<MessageCreateData>> onDataReceived();
    
    protected void getData(CommandEvent event, BiConsumer<CodeObject, Consumer<MessageCreateData>> onChosen) {
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
                    onChosen.accept(sameActions.get(0), event::reply);
                } else if (sameActions.size() > 1) {
                    sendMultipleMessage(sameActions, event, onChosen);
                }
                
                return;
                // Either there are too many similar actions or there is no close action.
            } else {
                List<CodeObject> similarObjects = possibleChoices.entrySet().stream()
                        .sorted(Map.Entry.<CodeObject, Double>comparingByValue().reversed())
                        .map(Map.Entry::getKey)
                        .toList();
                
                sendSelectMessage(similarObjects, String.format("I couldn't exactly find `%s`!", StringUtil.display(EmbedUtil.titleSafe(name))), "Here are some similar objects.", event, onChosen);
                return;
            }
            
        } else {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, String.format("Couldn't find anything that matched `%s`!", StringUtil.display(EmbedUtil.titleSafe(name))))
            );
        }
        event.reply(preset);
    }
    
    
}
