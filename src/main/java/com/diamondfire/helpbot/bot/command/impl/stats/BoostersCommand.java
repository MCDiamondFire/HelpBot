package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class BoostersCommand extends Command {

    @Override
    public String getName() {
        return "boosters";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"booster", "cpbooster", "boost", "boosts", "currentboosts", "cpboosts"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current active boosters on all nodes.")
                .category(CommandCategory.GENERAL_STATS);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Current Boosters", null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT * FROM xp_boosters WHERE FROM_UNIXTIME(end_time * 0.001) > CURRENT_TIMESTAMP()")
                .onQuery((resultTable) -> {
                    String owner = resultTable.getString("owner_name");
                    int multiplier = resultTable.getInt("multiplier");
                    String durationName = FormatUtil.formatMilliTime(resultTable.getLong("end_time") - System.currentTimeMillis());

                    embed.addField(String.format("%sx booster from %s ", multiplier, owner), String.format("Ends in: %s", durationName), false);
                }).onNotFound(() -> embed.addField("None!", "How sad... :(", false)).execute();
        event.reply(preset);

    }

}


