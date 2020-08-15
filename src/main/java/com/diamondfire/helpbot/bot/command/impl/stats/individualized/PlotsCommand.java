package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class PlotsCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "plots";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ownedplots", "plotlist"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current plots owned by a certain user. (Max of 25)")
                .category(CommandCategory.STATS)
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
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Owned Plots", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new SingleQueryBuilder()
                .query("SELECT * FROM plots WHERE owner_name = ? OR owner = ? LIMIT 25;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery((resultTablePlot) -> {
                    String formattedName = resultTablePlot.getString("owner_name");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName)
                    );

                    do {
                        String[] stats = {
                                "Votes: " + resultTablePlot.getInt("votes"),
                                "Players: " + resultTablePlot.getInt("player_count")
                        };
                        embed.addField(StringUtil.display(resultTablePlot.getString("name")) +
                                        String.format(" **(%s)**", resultTablePlot.getInt("id")),
                                String.join("\n", stats), false);

                    } while (resultTablePlot.next());
                })
                .onNotFound(() -> {
                    embed.clear();
                    preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found, or they have no plots."));
                }).execute();
        event.reply(preset);
    }

}

