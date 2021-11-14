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

import java.util.Objects;

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
    
        if (command instanceof SubCommandHolder subCommandHolder) {
            for (SubCommand subCommand : subCommandHolder.getSubCommands()) {
                SubcommandData subcommandData = new SubcommandData(subCommand.getName(), Objects.requireNonNullElse(subCommand.getHelpContext().getDescription(), desc));
                
                addOptions(subCommand, subcommandData);
                
                data.addSubcommands(subcommandData);
            }
        } else {
            addOptions(command, data);
        }
        
        return data;
    }
    
    private static void addOptions(Command command, CommandData commandData) {
        for (ArgumentNode<?> argument : command.getArguments().getArguments()) {
            OptionData optionData = convertArgument(argument);
            if (optionData != null) commandData.addOptions(optionData);
        }
    }
    
    private static void addOptions(Command command, SubcommandData commandData) {
        for (ArgumentNode<?> argument : command.getArguments().getArguments()) {
            OptionData optionData = convertArgument(argument);
            if (optionData != null) commandData.addOptions(optionData);
        }
    }
    
    private static OptionData convertArgument(ArgumentNode<?> node) {
        ArgumentContainer<?> container = node.getContainer();
        if (container instanceof SingleContainer singleContainer) {
            Argument<?> argument = singleContainer.getArgument();
            return argument.createOptionData(node.getIdentifier(), "noop", !container.isOptional());
        }
        
        // no impl for AlternateArgumentContainer yet
        return null;
    }
}
