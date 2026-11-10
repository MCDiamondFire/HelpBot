package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public class AttachmentArgument implements Argument<Message.Attachment> {

    @Override
    public Message.Attachment parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        if (event.getMessage() == null || event.getMessage().getAttachments().isEmpty()) {
            throw new MissingArgumentException("Expected an attachment, but got nothing.");
        }

        return event.getMessage().getAttachments().get(0);
    }

    @Override
    public Message.Attachment parseSlash(@NotNull OptionMapping option, CommandEvent event) {
        return option.getAsAttachment();
    }

    @Override
    public OptionData createOptionData(@NotNull String name, @NotNull String description, boolean required) {
        return new OptionData(OptionType.ATTACHMENT, name, description, required);
    }
}
