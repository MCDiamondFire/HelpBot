package com.diamondfire.helpbot.bot.command.slash;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;
import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.*;

public class SlashCommands {

    public static SlashCommandData create(Command command) {
        SlashCommandData data = Commands.slash(command.getName(), describe(command));
        if (command instanceof SubCommandHolder holder) {
            for (SubCommand subCommand : holder.getSubCommands()) {
                data.addSubcommands(new SubcommandData(subCommand.getName(), describe(subCommand)).addOptions(createOptions(subCommand)));
            }
        } else {
            data.addOptions(createOptions(command));
        }

        return data;
    }

    public static Command getCommand(String name, String subcommandName) {
        Command command = CommandHandler.getInstance().getCommands().get(name);
        if (subcommandName != null && command instanceof SubCommandHolder holder) {
            return getSubCommand(holder, subcommandName);
        }

        return command;
    }

    public static ParsedArgumentSet parseArguments(Command command, SlashCommandInteractionEvent interaction, CommandEvent event) throws ArgumentException {
        Map<String, ParsedArgument<?>> parsed = new HashMap<>();
        if (command instanceof SubCommandHolder holder) {
            parsed.put("subcommand", new ParsedArgument<>("subcommand", getSubCommand(holder, interaction.getSubcommandName())));
            return new ParsedArgumentSet(parsed);
        }

        List<ArgumentNode<?>> nodes = command.getArguments().getArguments();
        for (int i = 0; i < nodes.size(); i++) {
            ArgumentNode<?> node = nodes.get(i);
            String identifier = node.getIdentifier();
            ArgumentContainer<?> container = node.getContainer();
            OptionMapping option = interaction.getOption(optionName(identifier));

            try {
                if (option == null) {
                    if (!container.isOptional()) {
                        throw new MissingArgumentException("Expected an argument, but got nothing.");
                    }

                    parsed.put(identifier, new ParsedArgument<>(identifier, container.getDefaultValue()));
                } else if (container instanceof SingleArgumentContainer<?> single) {
                    parsed.put(identifier, new ParsedArgument<>(identifier, single.getArgument().parseSlash(option, event)));
                } else {
                    ArgumentStack stack = new ArgumentStack(List.of(node), Arrays.asList(option.getAsString().trim().split("\\s+")));
                    parsed.put(identifier, container.getParser().parse(identifier, stack.getRawArguments(), event));
                }
            } catch (ArgumentException exception) {
                if (exception instanceof MissingArgumentException && container.isOptional()) {
                    parsed.put(identifier, new ParsedArgument<>(identifier, container.getDefaultValue()));
                    continue;
                }
                
                exception.setContext(command, i, event);
                throw exception;
            }
        }

        return new ParsedArgumentSet(parsed);
    }

    public static List<String> complete(Command command, String optionName, String input) {
        String query = input.trim().toLowerCase(Locale.ROOT);
        return getArgument(command, optionName)
                .map(argument -> argument.getCompletions(input))
                .orElse(List.of())
                .stream()
                .filter(completion -> completion.length() <= OptionData.MAX_CHOICE_VALUE_LENGTH)
                .filter(completion -> completion.toLowerCase(Locale.ROOT).contains(query))
                .distinct()
                .sorted(Comparator.comparing((String completion) -> !completion.toLowerCase(Locale.ROOT).startsWith(query)))
                .limit(OptionData.MAX_CHOICES)
                .toList();
    }

    private static List<OptionData> createOptions(Command command) {
        List<ArgumentNode<?>> nodes = command.getArguments().getArguments();
        List<HelpContextArgument> help = command.getHelpContext().getArguments();
        List<OptionData> options = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            ArgumentNode<?> node = nodes.get(i);
            ArgumentContainer<?> container = node.getContainer();
            String name = optionName(node.getIdentifier());
            String helpName = i < help.size() ? help.get(i).getArgumentName() : null;
            String description = trim(helpName == null || helpName.isBlank() ? node.getIdentifier() : helpName);
            boolean required = !container.isOptional();

            OptionData option = container instanceof SingleArgumentContainer<?> single
                    ? single.getArgument().createOptionData(name, description, required)
                    : new OptionData(OptionType.STRING, name, description, required);
            if (option.getType() == OptionType.STRING && option.getChoices().isEmpty() && getArgument(command, name).map(Argument::hasCompletions).orElse(false)) {
                option.setAutoComplete(true);
            }

            options.add(option);
        }

        options.sort(Comparator.comparing(option -> !option.isRequired()));
        return options;
    }

    private static Optional<Argument<?>> getArgument(Command command, String optionName) {
        for (ArgumentNode<?> node : command.getArguments().getArguments()) {
            if (optionName(node.getIdentifier()).equals(optionName) && node.getContainer() instanceof SingleArgumentContainer<?> container) {
                return Optional.of(container.getArgument());
            }
        }
        
        return Optional.empty();
    }
    
    private static SubCommand getSubCommand(SubCommandHolder holder, String name) {
        return Arrays.stream(holder.getSubCommands())
                .filter(subCommand -> subCommand.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private static String optionName(String identifier) {
        return identifier.toLowerCase(Locale.ROOT);
    }

    private static String describe(Command command) {
        String description = command.getHelpContext().getDescription();
        return trim(description == null || description.isBlank() ? command.getName() : description);
    }

    private static String trim(String text) {
        text = text.replaceAll("\\s+", " ").trim();
        return text.length() > 100 ? text.substring(0, 97) + "..." : text;
    }
}
