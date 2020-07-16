package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.CommandCategory;
import com.diamondfire.helpbot.command.help.HelpContext;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
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
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("CP Leaderboard");
        new SingleQueryBuilder()
                .query("SELECT * FROM creator_rankings ORDER BY points DESC LIMIT 10")
                .onQuery((resultTable) -> {
                    do {
                        builder.addField(StringUtil.display(resultTable.getString("name")),
                                "CP: " + resultTable.getInt("points"), false);
                    } while (resultTable.next());
                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();

    }

}


