package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.bot.events.command.*;
import com.diamondfire.helpbot.util.FormatUtil;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

public abstract class SubCommandHolder extends Command {
    private Map<String, SubCommand> subCommands;

    public SubCommandHolder() {
        this.cacheSubCommands();
    }

    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }

    protected HelpContextArgument[] createHelpContextArguments() {
        return new HelpContextArgument[] {
            new HelpContextArgument()
                .name(FormatUtil.formatSubCommandOptions(this)),
            new HelpContextArgument()
                .name("...")
                .optional()
        };
    }

    @Override
    public void run(CommandEvent event) {
        throw new IllegalStateException("SubCommandHolder should never be executed as a command!");
    }

    // Cache subcommands so we don't have to loop when parsing.
    protected void cacheSubCommands() {
        HashMap<String, SubCommand> subCommandMap = new HashMap<>();

        for (SubCommand subCommand : getSubCommands()) {

            subCommandMap.put(subCommand.getName(), subCommand);
        }

        this.subCommands = Collections.unmodifiableMap(subCommandMap);
    }

    @ApiStatus.Internal
    @Nullable
    public SubCommand getSubCommand(String name) {
        return subCommands.get(name);
    }

    @Nullable
    public String getDefaultSubCommand() {
        return null;
    }

    public abstract SubCommand[] getSubCommands();
}
