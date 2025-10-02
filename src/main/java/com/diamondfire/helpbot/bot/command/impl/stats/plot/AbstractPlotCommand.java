package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.Nullable;

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
            Plot plot = getPlot(event);
            EmbedBuilder embed = preset.getEmbed();
            
            if (plot == null) {
                throw new IllegalStateException("Plot not found");
            }
            int plotID = plot.id();
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.INFO, String.format("Plot Information (%s)", plotID), null)
            );
            
            @Nullable String handle = plot.handle();
            @Nullable String description = plot.description();
            PlotSize size = plot.plotSize();
            
            embed.setTitle(handle == null ? String.format("Plot Information (%s)", plotID) : String.format("Plot Information (%s) (%s)", handle, plotID));
            embed.setDescription(description);
            embed.addField("Name", StringUtil.fromMiniMessage(plot.name()), true);
            embed.addField("Owner", plot.ownerName(), true);
            boolean privateNode = plot.node() >= 1000;
            embed.addField("Node", (privateNode ? "Private " : "") + "Node " + (privateNode ? plot.node() - 1000 : plot.node()), true);
            embed.addField("Plot Size", StringUtil.smartCaps(size.name()), true);
            
            // Creates a list of tags that the plot has
            String tags = plot.tags();
            if (tags.equals("none")) {
                embed.addField("Plot Tags", "None", true);
            } else {
                List<String> tagList = new ArrayList<>();
                for (String tag : tags.split(",")) {
                    tagList.add(plotTags.get(tag));
                }
                embed.addField("Plot Tags", StringUtil.listView("> ", true, tagList.toArray(new String[0])), true);
            }
            
            LocalDate activeTime = plot.activeTime();
            
            embed.addField("Last Active Date", FormatUtil.formatDate(activeTime), true);
            embed.addField("Whitelisted", plot.whitelisted() + "", true);
            embed.addField("Player Count", FormatUtil.formatNumber(plot.playerCount()), true);
            embed.addField("Current Votes", FormatUtil.formatNumber(plot.votes()), true);
            
            int x = plot.xMin() + (size.getSize() / 2);
            int z = plot.zMin() + (size.getSize() / 2);
            embed.addField("Plot Center", String.format("[%s, 50, %s]", x, z), true);
            
            // Creates the icon for the plot
            String plotIcon = plot.icon();
            // Plot head icons start with the character h.
            if (plotIcon.startsWith("h")) {
                embed.setThumbnail(Util.getPlayerHead(plotIcon.substring(1)));
                event.reply(preset);
            } else {
                File mcItem = Util.fetchMinecraftTextureFile(plotIcon.toUpperCase());
                embed.setThumbnail("attachment://" + mcItem.getName());
                event.getReplyHandler().replyA(preset).addFiles(FileUpload.fromData(mcItem)).queue();
            }
            
        } catch (IllegalStateException e) {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.ERROR, "Plot was not found.")
            );
            event.reply(preset);
        }
    }
    
    public abstract @Nullable Plot getPlot(CommandEvent event);
    
    protected Plot mapResultSetToPlot(final ResultSet rs) throws SQLException {
        return new Plot(
                rs.getInt("id"),
                rs.getString("handle"),
                rs.getString("description"),
                rs.getString("name"),
                rs.getString("owner_name"),
                rs.getInt("node"),
                PlotSize.fromID(rs.getInt("plotsize") - 1),
                rs.getString("tags"),
                rs.getInt("immunity_level"),
                rs.getDate("active_time").toLocalDate(),
                rs.getInt("whitelist") == 1,
                rs.getInt("player_count"),
                rs.getInt("votes"),
                rs.getInt("xmin"),
                rs.getInt("zmin"),
                rs.getString("icon")
        );
    }

}
