package com.diamondfire.helpbot.bot.command.slash;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ArgumentNode;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;
import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.commands.*;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.*;

public class SlashCommands {
    public static boolean requireMessageCommand(CommandEvent event, String desc) {
        if (!(event instanceof MessageCommandEvent)) {
            event.reply(new PresetBuilder().withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, desc)
            ));
            return true;
        }
        
        return false;
    }
    
    public static boolean requireMessageCommand(CommandEvent event) {
        return requireMessageCommand(event, "This command can only be run through a message!");
    }
    
    public static CommandData createCommandData(Command command) {
        CommandCategory category = command.getHelpContext().getCommandCategory();
        String desc = (category != null ? category.getName() : "Misc") + " - " + command.getHelpContext().getDescription();
        CommandData data = new CommandData(command.getName(), StringUtil.trim(desc, 100))
                .setDefaultEnabled(command.getPermission() == Permission.USER);
    
        try {
            if (command instanceof SubCommandHolder subCommandHolder) {
                for (SubCommand subCommand : subCommandHolder.getSubCommands()) {
                    SubcommandData subcommandData = new SubcommandData(subCommand.getName(), Objects.requireNonNullElse(subCommand.getHelpContext().getDescription(), desc));
                    
                    addOptions(sortArguments(subCommand), subcommandData);
                    
                    data.addSubcommands(subcommandData);
                }
            } else {
                addOptions(sortArguments(command), data);
            }
        } catch (Exception e) {
            System.err.println("Error creating command args (Could be subcommand!): " + command.getName());
            e.printStackTrace();
        }
        
        return data;
    }
    
    private static void addOptions(List<ArgumentNode<?>> args, CommandData commandData) {
        for (ArgumentNode<?> argument : args) {
            OptionData optionData = convertArgument(argument);
            if (optionData != null) commandData.addOptions(optionData);
        }
    }
    
    private static void addOptions(List<ArgumentNode<?>> args, SubcommandData commandData) {
        for (ArgumentNode<?> argument : args) {
            OptionData optionData = convertArgument(argument);
            if (optionData != null) commandData.addOptions(optionData);
        }
    }
    
    // Needed to move non-required args to the end for discord api.
    private static List<ArgumentNode<?>> sortArguments(Command command) {
        List<ArgumentNode<?>> original = command.getArguments().getArguments();
        
        List<ArgumentNode<?>> required = original.stream().filter(node -> !node.getContainer().isOptional()).toList();
        List<ArgumentNode<?>> optional = original.stream().filter(node -> node.getContainer().isOptional()).toList();
        
        List<ArgumentNode<?>> output = new ArrayList<>(required.size() + optional.size());
        output.addAll(required);
        output.addAll(optional);
        
        return output;
    }
    
    private static OptionData convertArgument(ArgumentNode<?> node) {
        try {
            ArgumentContainer<?> container = node.getContainer();
            if (container instanceof SingleContainer singleContainer) {
                Argument<?> argument = singleContainer.getArgument();
                return argument.createOptionData(node.getIdentifier().toLowerCase(Locale.ROOT), "noop", !container.isOptional());
            }
        } catch (Exception e) {
            System.err.println("Error creating argument: " + node.getIdentifier());
            e.printStackTrace();
        }
        
        // no impl for AlternateArgumentContainer yet
        return null;
    }
}
