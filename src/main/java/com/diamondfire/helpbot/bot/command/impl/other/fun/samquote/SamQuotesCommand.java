package com.diamondfire.helpbot.bot.command.impl.other.fun.samquote;

import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.bot.command.impl.SubCommand;
import com.diamondfire.helpbot.bot.command.impl.SubCommandHolder;
import com.diamondfire.helpbot.bot.command.permissions.Permission;

public class SamQuotesCommand extends SubCommandHolder {
    
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
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public String getDefaultSubCommand() {
        return "get";
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
