package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import com.diamondfire.helpbot.util.*;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class NamesCommand extends AbstractPlayerUUIDCommand {
    
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
                                .optional()
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder embed = preset.getEmbed();
        try {
            JsonObject profile = Util.getPlayerProfile(player.name());
        
            // Incase api fails. Shrug
            if (profile == null) {
                preset.withPreset(
                        new InformativeReply(InformativeReplyType.ERROR, "Player not found!")
                );
                event.reply(preset);
                return;
            }
        
            List<String> names = new ArrayList<>();
            String displayName = profile.get("name").getAsString();
            for (JsonElement nameElement : profile.get("name_history").getAsJsonArray()) {
                JsonObject obj = nameElement.getAsJsonObject();
                JsonElement changedAt = obj.get("changedToAt");
            
                preset.withPreset(
                        new MinecraftUserPreset(displayName),
                        new InformativeReply(InformativeReplyType.INFO, String.format("%s's Name Changes", displayName), null)
                );
            
                if (changedAt == null) {
                    names.add(String.format("%s", obj.get("name").getAsString()));
                } else {
                    names.add(obj.get("name").getAsString() + String.format(" (%s)", FormatUtil.formatDate(DateUtil.toDate(changedAt.getAsLong()))));
                }
            
            }
            Collections.reverse(names);
        
            EmbedUtil.addFields(embed, names);
        } catch (Exception e) {
            e.printStackTrace();
        }
        event.reply(preset);
    }
    
}


