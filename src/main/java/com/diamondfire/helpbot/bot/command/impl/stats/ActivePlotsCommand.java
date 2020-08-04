package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("SpellCheckingInspection")
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
                .category(CommandCategory.STATS);
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
                        new InformativeReply(InformativeReplyType.INFO, "Active Plots", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new SingleQueryBuilder()
                .query("SELECT * FROM plots WHERE player_count > 0 AND whitelist = 0 ORDER BY player_count DESC LIMIT 10")
                .onQuery((resultTable) -> {
                    do {
                        embed.addField(StringUtil.display(resultTable.getString("name")) +
                                        String.format(" **(%s)**", resultTable.getInt("id")),
                                "Players: " + resultTable.getInt("player_count"), false);
                    } while (resultTable.next());
                }).execute();

        event.reply(preset);
    }

}


