package com.diamondfire.helpbot.bot.command.impl.other.info;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.DefinedObjectArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.EmbedUtil;

import java.util.*;

public class PermUnlocksCommand extends Command {
    
    @Override
    public String getName() {
        return "permunlocks";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a list of commands that is unlocked by a permission.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("perm")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("permission",
                        new DefinedObjectArgument<>(Rank.values())
                );
    }
    
    @Override
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Rank permission = event.getArgument("permission");
        PresetBuilder builder = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Commands unlocked by: " + permission, null)
                );
        
        List<String> commands = new ArrayList<>();
        List<Command> commandList = new ArrayList<>(CommandHandler.getInstance().getCommands().values());
        commandList.sort(Comparator.comparingInt((command) -> command.getRank().ordinal()));
        
        for (Command command : commandList) {
            if (command.getRank().hasPermission(permission)) {
                StringBuilder cmdName = new StringBuilder();
                cmdName.append(command.getName());
                
                if (command.getRank() != permission) {
                    cmdName.append(" (From ")
                            .append(command.getRank())
                            .append(')');
                }
                
                commands.add(cmdName.toString());
            }
        }
        
        EmbedUtil.addFields(builder.getEmbed(), commands);
        event.reply(builder);
    }
    
}
