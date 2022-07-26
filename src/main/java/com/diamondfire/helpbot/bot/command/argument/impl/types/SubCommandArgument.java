package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SubCommandArgument extends AbstractSimpleValueArgument<SubCommand> {
    
    @Override
    public SubCommand parse(@NotNull String msg, CommandEvent event) throws ArgumentException {
        try {
            return Arrays.stream(((SubCommandHolder) event.getBaseCommand()).getSubCommands())
                    .filter(s -> s.getName().equals(msg)).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedArgumentException(
                    "Not a valid subcommand. Choose from " + event.getBaseCommand().getHelpContext().getArguments().get(0).getArgumentName());
        }
    }
    
    @Override
    public OptionData createOptionData(@NotNull String name, @NotNull String description, boolean isRequired) {
        throw new IllegalStateException("SubCommandArgument should never be converted in slash commands."); // We have special handling for other args.
    }
}
