package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.MalformedArgumentException;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.jetbrains.annotations.NotNull;

import java.util.regex.*;

public class DiscordMentionArgument extends AbstractSimpleValueArgument<Long> {
    private final Message.MentionType mentionType;
    
    private DiscordMentionArgument(Message.MentionType mentionType) {
        this.mentionType = mentionType;
    }
    
    // these are to make sure the user only uses id-based mentions,
    // JDA's MentionType is delegated to internally so we don't have to do the heavy lifting with regex
    public static DiscordMentionArgument user() {
        return new DiscordMentionArgument(Message.MentionType.USER);
    }
    
    public static DiscordMentionArgument role() {
        return new DiscordMentionArgument(Message.MentionType.ROLE);
    }
    
    public static DiscordMentionArgument channel() {
        return new DiscordMentionArgument(Message.MentionType.CHANNEL);
    }
    
    @Override
    public Long parse(@NotNull String msg, CommandEvent event) throws MalformedArgumentException {
        try {
            return Long.parseLong(msg);
        } catch (NumberFormatException ignored) {
        }
        
        Matcher matcher = mentionType.getPattern().matcher(msg);
        if (matcher.find()) {
            return MiscUtil.parseSnowflake(matcher.group(1));
        }
        throw new MalformedArgumentException("Bad mention argument provided, must either be a mention or an id.");
    }
}
