package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.IntegerArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.File;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PlotCommand extends Command {

    @Override
    public String getName() {
        return "plot";
    }

    @Override
    public String getDescription() {
        return "Get info on a certain plot.";
    }

    @Override
    public ValueArgument<Integer> getArgument() {
        return new IntegerArg("Plot ID", true);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        int plotID = getArgument().getArg(event.getParsedArgs());


        new SingleQueryBuilder()
                .query("SELECT * FROM plots where ID = ?;", (statement) -> {
                    statement.setInt(1, plotID);
                })
                .onQuery((resultTablePlot) -> {

                    builder.addField("Name", StringUtil.stripColorCodes(resultTablePlot.getString("name")), false);
                    builder.addField("Owner", resultTablePlot.getString("owner_name"), false);
                    builder.addField("Node", "Node " + resultTablePlot.getInt("node"), false);
                    builder.addField("Plot Size", StringUtil.smartCaps(PlotSize.getFromSize(resultTablePlot.getInt("plotsize")).name()), false);

                    String tags = resultTablePlot.getString("tags");

                    if (tags.equals("none")) {
                        builder.addField("Plot Tags", "None", true);
                    } else {
                        ArrayList<String> tagList = new ArrayList<>();
                        for (String tag : tags.split(",")) {
                            tagList.add(StringUtil.smartCaps(PlotTag.getFromIdentifier(tag).name()));
                        }

                        builder.addField("Plot Tags", StringUtil.listView(tagList.toArray(new String[0]), ">", true), false);
                    }
                    int weeksTillClear = resultTablePlot.getInt("immunity_level");
                    builder.addField("Auto Clear Date", formatDate(new Date(LocalDateTime.now().plus(weeksTillClear, ChronoUnit.WEEKS).toInstant(ZoneOffset.UTC).toEpochMilli())) + String.format(" (%s weeks)", weeksTillClear), false);
                    builder.addField("Last Active Date", formatDate(resultTablePlot.getDate("active_time")), false);
                    builder.addField("Whitelisted", String.valueOf(resultTablePlot.getInt("whitelist") == 1), false);
                    builder.addField("Player Count", resultTablePlot.getInt("player_count") + "", false);
                    builder.addField("Current Votes", resultTablePlot.getInt("votes") + "", false);

                    String plotIcon = resultTablePlot.getString("icon");
                    File icon;
                    if (plotIcon.charAt(0) == 'h') {
                        icon = Util.getPlayerHead(plotIcon.substring(1));

                        if (!icon.exists()) {
                            Util.fetchMinecraftTextureFile(plotIcon.toLowerCase());
                        }
                    } else {
                        icon = Util.fetchMinecraftTextureFile(plotIcon.toLowerCase());
                    }

                    builder.setTitle(String.format("Plot Information (%s)", plotID));
                    builder.setThumbnail("attachment://" + icon.getName() + ".png");
                    event.getChannel().sendMessage(builder.build()).addFile(icon, icon.getName() + ".png").queue();


                })
                .onNotFound(() -> {
                    builder.setTitle("Error!");
                    builder.setDescription("Plot was not found");
                    event.getChannel().sendMessage(builder.build()).queue();
                }).execute();




    }


    public String format(int millis) {
        StringBuilder builder = new StringBuilder();

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        if (hours > 0) {
            builder.append(hours + "h");
        }

        long mins = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        if (mins > 0) {
            builder.append(" " + mins + "m");
        }
        long secs = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        if (secs != 0) {
            builder.append(" " + secs + "s");
        } else {
            builder.append("0." + TimeUnit.MILLISECONDS.toMillis(millis) + "s");
        }

        return builder.toString();

    }

    @SuppressWarnings("deprecation")
    public String formatDate(Date date) {
        return date.toLocalDate().getDayOfMonth() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900);
    }

}

enum PlotTag {
    ARCADE("ac"),
    VERSUS("vs"),
    COMBAT("cb"),
    PARKOUR("pk"),
    ADVENTURE("av"),
    ROLEPLAY("rp"),
    STRATEGY("sy"),
    PUZZLE("pu"),
    QUIZ("qz"),
    RESOURCES("rs"),
    ELIMINATION("em"),
    CREATION("bd"),
    MISCELLANEOUS("ms");

    private static final HashMap<String, PlotTag> TAGS = new HashMap<>();

    static {
        for (PlotTag tag : values()) {
            TAGS.put(tag.getIdentifier(), tag);
        }
    }

    private String identifier;

    PlotTag(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public static PlotTag getFromIdentifier(String identifier) {
        return TAGS.get(identifier);
    }
}

enum PlotSize {
    BASIC(1),
    LARGE(2),
    MASSIVE(3);

    private static final HashMap<Integer, PlotSize> SIZES = new HashMap<>();

    static {
        for (PlotSize tag : values()) {
            SIZES.put(tag.getSize(), tag);
        }
    }

    private int size;

    PlotSize(int identifier) {
        this.size = identifier;
    }

    public int getSize() {
        return size;
    }

    public static PlotSize getFromSize(int size) {
        return SIZES.get(size);
    }
}
