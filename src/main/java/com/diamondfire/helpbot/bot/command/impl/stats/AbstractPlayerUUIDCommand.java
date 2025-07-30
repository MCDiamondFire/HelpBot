package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;

public abstract class AbstractPlayerUUIDCommand extends Command {
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("player",
                        new SingleArgumentContainer<>(new DFPlayerArgument()).optional(null));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Player player = event.getArgument("player");
        if (player == null) {
            // If no player is specified, use the member's name,
            // however, non-verified members can set their display name
            // to any player and run commands as that player; therefore,
            // we need to check if the member is verified.
            if (!event.getMember().getRoles().contains(event.getGuild().getRoleById(Util.VERIFIED))) {
                PresetBuilder preset = new PresetBuilder()
                        .withPreset(
                                new InformativeReply(InformativeReplyType.ERROR, "You must be verified to use this command!")
                        );
                event.reply(preset);
                return;
            }
            execute(event, DFPlayerArgument.fetchPlayer(event.getMember().getEffectiveName()));
        } else {
            execute(event, player);
        }
    }
    
    protected abstract void execute(CommandEvent event, Player player);
    
}


