package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class CpTopCommand extends Command {

    @Override
    public String getName() {
        return "cptop";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cpleaderboard"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current CP leaderboard.")
                .category(CommandCategory.STATS)
                .addArgument(new HelpContextArgument().name("leaderboard place").optional());
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet().addArgument("place",
                new SingleArgumentContainer<>(new ClampedIntegerArgument(1)).optional(1)
        );
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        int startingPlace = event.getArgument("place");
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "CP Leaderboard", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new SingleQueryBuilder()
                .query("SELECT * FROM creator_rankings ORDER BY points DESC LIMIT 10 OFFSET ?", (statement -> statement.setInt(1, startingPlace - 1)))
                .onQuery((resultTable) -> {
                    int place = startingPlace;
                    do {
                        embed.addField(String.format("%s. ", StringUtil.formatNumber(place)) + StringUtil.display(resultTable.getString("name")),
                                "CP: " + StringUtil.formatNumber(resultTable.getInt("points")), false);
                        place++;
                    } while (resultTable.next());
                }).execute();

        event.reply(preset);
    }

}


