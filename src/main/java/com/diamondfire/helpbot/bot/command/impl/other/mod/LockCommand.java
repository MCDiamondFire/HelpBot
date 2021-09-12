package com.diamondfire.helpbot.bot.command.impl.other.mod;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.EndlessStringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;

public class LockCommand extends Command {
    //TODO
    @Override
    public String getName() {
        return "lock";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{
                "unlock"
        };
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Toggles a channel lock.")
                .addArgument(
                    new HelpContextArgument()
                            .name("channel")
                            .optional()
                ).addArgument(
                    new HelpContextArgument()
                            .name("reason")
                            .optional()
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument(
                        "channel",
                            new TextChannelArgument(Permission.MANAGE_PERMISSIONS)
                ).addArgument(
                        "reason",
                            new SingleArgumentContainer<>(new EndlessStringArgument())
                                    .optional(event -> "No reason provided.")
                );
    }
    
    @Override
    public Rank getRank() {
        return Rank.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        toggleLock(
                event.getArgument("channel"),
                event.getArgument("reason")
        );
        
        event.getMessage().addReaction("✅").queue();
    }
    
    private static final Permission[] LOCK_PERMISSIONS = new Permission[]{
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_ADD_REACTION
    };
    
    private void toggleLock(TextChannel channel, String reason) {
        if (isLocked(channel)) {
            unlockChannel(channel, reason);
        } else {
            lockChannel(channel, reason);
        }
    }
    
    @SuppressWarnings("all")
    private boolean isLocked(TextChannel channel) {
        return !channel.getPermissionOverride(Rank.USER.getRole()).getAllowed()
                .containsAll(Arrays.asList(LOCK_PERMISSIONS));
    }
    
    private void lockChannel(TextChannel channel, String reason) {
        channel
                .putPermissionOverride(Rank.USER.getRole())
                .deny(LOCK_PERMISSIONS)
                .queue();
        
        channel.sendMessageEmbeds(new PresetBuilder()
                .withPreset(new InformativeReply(
                        InformativeReplyType.INFO,
                        "This channel has been locked.\nReason: " + reason
                )).getEmbed().build()).queue();
    }
    
    private void unlockChannel(TextChannel channel, String reason) {
        channel
                .putPermissionOverride(Rank.USER.getRole())
                .grant(LOCK_PERMISSIONS)
                .queue();
        
        channel.sendMessage(":unlock: This channel has been unlocked.").queue();
    }
}
