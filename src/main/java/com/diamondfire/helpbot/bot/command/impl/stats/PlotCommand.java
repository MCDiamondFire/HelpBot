package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.IntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.File;
import java.sql.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlotCommand extends Command {


    private static final HashMap<String, String> plotTags = new HashMap<>();
    private static final HashMap<Integer, String> plotSizes = new HashMap<>();

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

        plotSizes.put(1, "Basic");
        plotSizes.put(2, "Large");
        plotSizes.put(3, "Massive");
    }

    @Override
    public String getName() {
        return "plot";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information on a certain plot.")
                .category(CommandCategory.STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("plot id")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("id",
                        new IntegerArgument());
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        int plotID = event.getArgument("id");
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("Plot Information (%s)", plotID), null)
                );
        EmbedBuilder embed = preset.getEmbed();


        new SingleQueryBuilder()
                .query("SELECT * FROM plots where ID = ?;", (statement) -> {
                    statement.setInt(1, plotID);
                })
                .onQuery((resultTablePlot) -> {
                    embed.addField("Name", StringUtil.display(resultTablePlot.getString("name")), true);
                    embed.addField("Owner", resultTablePlot.getString("owner_name"), true);
                    embed.addField("Node", "Node " + resultTablePlot.getInt("node"), true);
                    embed.addField("Plot Size", plotSizes.get(resultTablePlot.getInt("plotsize")), true);

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
                    embed.addField("Auto Clear Date", StringUtil.formatDate(new Date(LocalDateTime.now().plus(weeksTillClear, ChronoUnit.WEEKS).toInstant(ZoneOffset.UTC).toEpochMilli())) + String.format(" (%s weeks)", weeksTillClear), true);
                    embed.addField("Last Active Date", StringUtil.formatDate(resultTablePlot.getDate("active_time")), true);
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
                })
                .onNotFound(() -> {
                    preset.withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "Plot was not found.")
                    );
                    event.reply(preset);
                }).execute();
    }

}