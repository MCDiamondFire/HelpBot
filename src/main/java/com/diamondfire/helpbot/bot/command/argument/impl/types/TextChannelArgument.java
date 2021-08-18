package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <strong>This argument extends {@link FallbackArgument}.</strong>
 *
 * Will return the channel this command was executed in if an invalid channel was provided by the user.
 */
public class TextChannelArgument extends AbstractSimpleValueFallbackArgument<TextChannel> {
    
    private final Permission[] requiredChannelPermissions;
    
    public TextChannelArgument() {
        this(Permission.EMPTY_PERMISSIONS);
    }
    
    public TextChannelArgument(Permission... requiredChannelPermissions) {
        this.requiredChannelPermissions = requiredChannelPermissions;
    }
    
    @Override
    protected TextChannel parse(@NotNull String argument, CommandEvent event) throws ArgumentException {
    
        List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
        
        for (TextChannel channel : mentionedChannels) {
            if (channel.getAsMention().equals(argument) || channel.getId().equals(argument)) {
                if (!event.getGuild().getSelfMember().hasPermission(channel, requiredChannelPermissions)) {
                    
                    throw new MalformedArgumentException(
                            "I need the following permissions in this channel: " +
                                    getFormattedPermissionList(requiredChannelPermissions)
                    );
                }
                
                return channel;
            }
        }
        
        throw new MalformedArgumentException("Invalid channel provided. Must be a valid mention or ID.");
    }
    
    @Override
    public TextChannel ifFail(CommandEvent event) {
        return event.getChannel();
    }
    
    public static @NotNull String getFormattedPermissionList(Permission... permissions) {
        return "`" + Arrays.stream(permissions)
                .map(Permission::getName)
                .collect(Collectors.joining("` `"))
                + "`";
    }

}
