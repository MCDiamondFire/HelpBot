package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.StringUtil;
import com.diamondfire.helpbot.util.PlotIconUtil;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivePlotsCommand extends Command {
    
    @Override
    public String getName() {
        return "activeplots";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"nowplaying"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current plots that people are playing.")
                .category(CommandCategory.GENERAL_STATS);
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
        new DatabaseQuery()
                .query(new BasicQuery("SELECT id, name, owner_name, player_count, icon FROM plots " +
                        "WHERE player_count > 0 AND whitelist = 0 ORDER BY player_count DESC, id ASC LIMIT 10"))
                .compile()
                .run((result) -> {
                    List<ActivePlot> plots = new ArrayList<>();
                    for (ResultSet set : result) {
                        plots.add(new ActivePlot(set.getInt("id"), set.getString("name"),
                                set.getString("owner_name"), set.getInt("player_count"), set.getString("icon")));
                    }
                    event.reply(createMessage(plots));
                });
    }

    static MessageCreateData createMessage(List<ActivePlot> plots) {
        List<ContainerChildComponent> components = new ArrayList<>();
        components.add(TextDisplay.of("## Now Playing"));
        components.add(Separator.createDivider(Separator.Spacing.SMALL));

        if (plots.isEmpty()) {
            components.add(TextDisplay.of("**No public plots are active right now.**\nCheck back soon for something to play!"));
        } else {
            // Ten sections with thumbnails stay within Discord's 40-component limit.
            int count = Math.min(plots.size(), 10);
            for (int index = 0; index < count; index++) {
                ActivePlot plot = plots.get(index);
                String name = displayText(plot.name(), "Unnamed plot", 120);
                String owner = displayText(plot.owner(), "Unknown", 40);
                TextDisplay details = TextDisplay.of("### " + (index + 1) + ". " + name + "\n**" +
                        plot.players() + (plot.players() == 1 ? " player" : " players") +
                        "** · by " + owner + "\n`" + plot.id() + "`");
                // Unique attachment names also support plots sharing the same material icon.
                Thumbnail icon = PlotIconUtil.createThumbnail(plot.icon(), "plot-icon-" + index + ".png");
                components.add(icon == null ? details : Section.of(icon, details));
            }
        }

        return new MessageCreateBuilder()
                .useComponentsV2()
                .setComponents(Container.of(components).withAccentColor(0x5865F2))
                .setAllowedMentions(Collections.emptySet())
                .build();
    }

    private static String displayText(String value, String fallback, int limit) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        // Keep user-provided names on one line and the entire message below 4,000 characters.
        String text = StringUtil.fromMiniMessage(value).replaceAll("\\s+", " ").trim();
        if (text.isEmpty()) {
            return fallback;
        }
        return text.length() > limit ? text.substring(0, limit - 1) + "…" : text;
    }

    record ActivePlot(int id, String name, String owner, int players, String icon) {}
}


