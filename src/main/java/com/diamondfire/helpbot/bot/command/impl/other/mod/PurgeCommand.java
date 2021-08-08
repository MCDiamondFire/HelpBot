package com.diamondfire.helpbot.bot.command.impl.other.mod;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.entities.*;

import java.util.List;

public class PurgeCommand extends Command {
    
    @Override public String getName() { return "purge"; }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Removes the most recent messages sent in a channel. Maxiumum 100 messages.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("count")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("count",
                        new IntegerArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        
        int messagesToRemove = event.getArgument("count");
        
        if (messagesToRemove > 100 || messagesToRemove < 0) {
            builder.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Message count not within 0 to 100.")
            );
            event.reply(builder);
        } else {
            TextChannel channel = event.getChannel();
            List<Message> messages = channel.getHistory().retrievePast(messagesToRemove).complete();
            channel.deleteMessages(messages).complete();
        }
    }
}
