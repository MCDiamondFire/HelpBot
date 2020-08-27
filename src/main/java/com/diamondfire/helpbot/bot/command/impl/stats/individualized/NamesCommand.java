package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.*;
import java.net.*;
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
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder embed = preset.getEmbed();
        try {
            URL profile = new URL("https://mc-heads.net/minecraft/profile/" + player);
            URLConnection connection = profile.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    stringBuilder.append(inputLine);
            }

            if (stringBuilder.toString().length() == 0) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Player not found!");

                event.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            List<String> names = new ArrayList<>();
            JsonObject element = JsonParser.parseString(stringBuilder.toString()).getAsJsonObject();
            String displayName = element.get("name").getAsString();
            for (JsonElement nameElement : element.get("name_history").getAsJsonArray()) {
                JsonObject obj = nameElement.getAsJsonObject();
                JsonElement changedAt = obj.get("changedToAt");

                preset.withPreset(
                        new MinecraftUserPreset(displayName),
                        new InformativeReply(InformativeReplyType.INFO, String.format("%s's Name Changes", displayName), null)
                );

                if (changedAt == null) {
                    names.add(String.format("%s", obj.get("name").getAsString()));
                } else {
                    names.add(obj.get("name").getAsString() + String.format(" (%s)", StringUtil.formatDate(new Date(changedAt.getAsLong()))));
                }

            }
            Collections.reverse(names);

            Util.addFields(embed, names);
        } catch (Exception e) {
            e.printStackTrace();
        }
        event.reply(preset);
    }

}


