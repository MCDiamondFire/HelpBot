package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public abstract class AbstractPlotCommand extends Command {
    
    private static final HashMap<String, String> plotTags = new HashMap<>();
    
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
            
            PlotSize size = PlotSize.fromID(resultTablePlot.getInt("plotsize") - 1);
            
            embed.setTitle(String.format("Plot Information (%s)", plotID));
            embed.addField("Name", StringUtil.display(resultTablePlot.getString("name")), true);
            embed.addField("Owner", resultTablePlot.getString("owner_name"), true);
            embed.addField("Node", "Node " + resultTablePlot.getInt("node"), true);
            embed.addField("Plot Size", StringUtil.smartCaps(size.name()), true);
            
            // Creates a list of tags that the plot has
            String tags = resultTablePlot.getString("tags");
            if (tags.equals("none")) {
                embed.addField("Plot Tags", "None", true);
            } else {
                List<String> tagList = new ArrayList<>();
                for (String tag : tags.split(",")) {
                    tagList.add(plotTags.get(tag));
                }
                embed.addField("Plot Tags", StringUtil.listView("> ", true, tagList.toArray(new String[0])), true);
            }
            
            int weeksTillClear = resultTablePlot.getInt("immunity_level");
            LocalDate activeTime = resultTablePlot.getDate("active_time").toLocalDate();
            LocalDate clearDate = activeTime.plus(weeksTillClear, ChronoUnit.WEEKS);
            
            embed.addField("Auto Clear Date", FormatUtil.formatDate(clearDate) + String.format(" (%s weeks)", ChronoUnit.WEEKS.between(activeTime, clearDate)), true);
            embed.addField("Last Active Date", FormatUtil.formatDate(activeTime), true);
            embed.addField("Whitelisted", (resultTablePlot.getInt("whitelist") == 1) + "", true);
            embed.addField("Player Count", FormatUtil.formatNumber(resultTablePlot.getInt("player_count")), true);
            embed.addField("Current Votes", FormatUtil.formatNumber(resultTablePlot.getInt("votes")), true);
            
            int x = resultTablePlot.getInt("xmin") + (size.getSize() / 2);
            int z = resultTablePlot.getInt("zmin") + (size.getSize() / 2);
            embed.addField("Plot Center", String.format("[%s, 50, %s]", x, z), true);
            
            // Creates the icon for the plot
            String plotIcon = resultTablePlot.getString("icon");
            // Plot head icons start with the character h.
            if (plotIcon.startsWith("h")) {
                embed.setThumbnail(Util.getPlayerHead(plotIcon.substring(1)));
                event.reply(preset);
            } else {
                File mcItem = Util.fetchMinecraftTextureFile(plotIcon.toUpperCase());
                embed.setThumbnail("attachment://" + mcItem.getName());
                event.getReplyHandler().replyA(preset).addFiles(FileUpload.fromData(mcItem)).queue();
            }
            
        } catch (SQLException | IllegalStateException e) {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Plot was not found.")
            );
            event.reply(preset);
        }
    }
    
    public abstract ResultSet getPlot(CommandEvent event);
    
    
    private enum PlotSize {
        BASIC(51),
        LARGE(101),
        MASSIVE(301);
        
        private final int size;
        
        PlotSize(int size) {
            this.size = size;
        }
        
        public int getSize() {
            return size;
        }
        
        public static PlotSize fromID(int id) {
            return PlotSize.values()[id];
        }
    }
}
