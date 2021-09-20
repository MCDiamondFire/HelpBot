package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class NamesCommand extends Command {
    
    @Override
    public String getName() {
        return "names";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"namehistory", "prevnames", "oldnames"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a user's previous names.")
                .category(CommandCategory.PLAYER_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                );
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("player|uuid",
                        new SingleArgumentContainer<>(new StringArgument()));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder embed = preset.getEmbed();
        try {
            UserProfile profile = Util.getPlayerProfile(event.getArgument("player|uuid"));
        
            // Incase api fails. Shrug
            if (profile == null) {
                preset.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Player not found!")
                );
                event.reply(preset);
                return;
            }
        
            List<String> names = new ArrayList<>();
            String displayName = profile.getUsername();
            for (Pair<String, Long> nameChange : profile.getNameHistory()) {
                Long changedAt = nameChange.getSecond();
            
                preset.withPreset(
                        new MinecraftUserPreset(displayName),
                        new InformativeReply(InformativeReplyType.INFO, String.format("%s's Name Changes", displayName), null)
                );
            
                if (changedAt == null) {
                    names.add(String.format("%s", nameChange.getFirst()));
                } else {
                    names.add(nameChange.getFirst() + String.format(" (%s)", FormatUtil.formatDate(DateUtil.toDate(changedAt))));
                }
            
            }
            Collections.reverse(names);
        
            EmbedUtil.addFields(embed, names);
        } catch (Exception e) {
            e.printStackTrace();
        }
        preset.withPreset(
                new InformativeReply(InformativeReplyType.ERROR, "Player not found!")
        );
        event.reply(preset);
    }
    
}


