package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.File;
import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public abstract class AbstractPlotCommand extends Command {

    private static final HashMap<String, String> plotTags = new HashMap<>();
    private static final List<String> plotSizes = new ArrayList<>();

    static {
        plotTags.put("ac", "Arcade");
        plotTags.put("vs", "Versus");
        plotTags.put("cb", "Combat");
        plotTags.put("pk", "Parkour");
        plotTags.put("av", "Adventure");
        plotTags.put("rp", "Roleplay");
        plotTags.put("sy", "Strategy");
        plotTags.put("pu", "Puzzle");
        plotTags.put("qz", "Quiz");
        plotTags.put("rs", "Resources");
        plotTags.put("em", "Elimination");
        plotTags.put("bd", "Creation");
        plotTags.put("ms", "Miscellaneous");

        plotSizes.add("Basic");
        plotSizes.add("Large");
        plotSizes.add("Massive");
    }

    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder();
        try {
            ResultSet resultTablePlot = getPlot(event);
            EmbedBuilder embed = preset.getEmbed();

            if (resultTablePlot == null || !resultTablePlot.next()) {
                throw new IllegalStateException("Plot not found");
            }
            int plotID = resultTablePlot.getInt("id");
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.INFO, String.format("Plot Information (%s)", plotID), null)
            );

            embed.addField("Name", StringUtil.display(resultTablePlot.getString("name")), true);
            embed.addField("Owner", resultTablePlot.getString("owner_name"), true);
            embed.addField("Node", "Node " + resultTablePlot.getInt("node"), true);
            embed.addField("Plot Size", plotSizes.get(resultTablePlot.getInt("plotsize") - 1), true);

            // Creates a list of tags that the plot has
            String tags = resultTablePlot.getString("tags");
            if (tags.equals("none")) {
                embed.addField("Plot Tags", "None", true);
            } else {
                List<String> tagList = new ArrayList<>();
                for (String tag : tags.split(",")) {
                    tagList.add(plotTags.get(tag));
                }
                embed.addField("Plot Tags", StringUtil.listView(tagList.toArray(new String[0]), ">", true), true);
            }

            int weeksTillClear = resultTablePlot.getInt("immunity_level");
            LocalDate activeTime = DateUtil.toLocalDate(resultTablePlot.getDate("active_time"));

            embed.addField("Auto Clear Date", StringUtil.formatDate(DateUtil.toDate(activeTime.plus(weeksTillClear, ChronoUnit.WEEKS))) + String.format(" (%s weeks)", weeksTillClear), true);
            embed.addField("Last Active Date", StringUtil.formatDate(DateUtil.toDate(activeTime)), true);
            embed.addField("Whitelisted", (resultTablePlot.getInt("whitelist") == 1) + "", true);
            embed.addField("Player Count", StringUtil.formatNumber(resultTablePlot.getInt("player_count")), true);
            embed.addField("Current Votes", StringUtil.formatNumber(resultTablePlot.getInt("votes")), true);

            // Creates the icon for the plot
            String plotIcon = resultTablePlot.getString("icon");
            File icon;
            // Plot head icons start with the character h.
            if (plotIcon.startsWith("h")) {
                icon = Util.getPlayerHead(plotIcon.substring(1));
            } else {
                icon = Util.fetchMinecraftTextureFile(plotIcon.toUpperCase());
            }
            embed.setTitle(String.format("Plot Information (%s)", plotID));
            embed.setThumbnail("attachment://" + icon.getName() + ".png");

            event.replyA(preset).addFile(icon, icon.getName() + ".png").queue();
        } catch (SQLException | IllegalStateException e) {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Plot was not found.")
            );
            event.reply(preset);
        }
    }

    public abstract ResultSet getPlot(CommandEvent event);

}
