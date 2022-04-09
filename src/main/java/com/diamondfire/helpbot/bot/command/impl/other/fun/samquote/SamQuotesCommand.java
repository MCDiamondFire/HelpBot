package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.SubCommandArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.Random;

public class SamQuotesCommand extends SubCommandHolder {
    
    private static final Random random = new Random();
    
    @Override
    public String getName() {
        return "samquote";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a quote from Sam the Man.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("get/submit/generate/reload/count")
                                .optional(),
                        new HelpContextArgument()
                                .name("...")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument(
                "subcommand", new SingleArgumentContainer<>(new SubCommandArgument()).optional(null)
        );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        if (event.getArgument("subcommand") == null) {
            GetSamquoteSubCommand.runStatic(event);
        } else {
            super.run(event);
        }
    }
    
    @Override
    public SubCommand[] getSubCommands() {
        return new SubCommand[] {
                new ReloadSamquotesSubCommand(),
                new CountSamquotesSubCommand(),
                new SubmitSamquoteSubCommand(),
                new GenerateSamquoteSubCommand(),
                new GetSamquoteSubCommand()
        };
    }
}