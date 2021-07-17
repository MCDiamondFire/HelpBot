package com.diamondfire.helpbot.bot.command.impl.other.tag.impl;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.other.tag.TagHandler;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.*;

import java.io.IOException;
import java.util.Set;

public class ListTagsCommand extends Command {
    
    @Override
    public String getName() {
        return "listtags";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Lists all custom command tags.")
                .category(CommandCategory.OTHER);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        try {
            JsonObject tags = TagHandler.getTags();
            String string = "";
            Set<String> keySet = tags.keySet();
            
            if (keySet.size() == 0) {
                string = "*None*";
                
            } else {
                for (String key : keySet) {
                    JsonObject tag = tags.get(key).getAsJsonObject();
                    string += "`" + tag.get("activator").getAsString() + "` ";
                }
            }
            
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.INFO, "Tags",
                                    "A list of all custom command tags added.\n\n"+string+"\n\u200b")
                    );
            
            User ryanland = event.getJDA().retrieveUserById(808966728201666620L).complete();
            MessageEmbed embed = preset.getEmbed().setFooter("Tag system by "+ryanland.getAsTag(), ryanland.getAvatarUrl())
                    .build();
            event.getChannel().sendMessage(embed).queue();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}


