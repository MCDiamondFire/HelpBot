package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;

public class PlotsCommand extends Command {

    @Override
    public String getName() {
        return "plots";
    }

    @Override
    public String getDescription() {
        return "Gets current plots owned by user.";
    }

    @Override
    public ValueArgument<String> getArgument() {
        return new StringArg("Player Name", false);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        String nameToSelect = getArgument().getArg(event.getParsedArgs());

        if (event.getParsedArgs().isEmpty()) {
            nameToSelect = event.getMember().getEffectiveName();
        }

        final String name = nameToSelect;
        new SingleQueryBuilder()
                .query("SELECT * FROM plots WHERE owner_name = ? OR owner = ? LIMIT 25;", (statement) -> {
                    statement.setString(1, name);
                    statement.setString(2, name);
                })

                .onQuery((resultTablePlot) -> {
                    do {
                        ArrayList<String> stats = new ArrayList<>();
                        stats.add("Votes: " + resultTablePlot.getInt("votes"));
                        stats.add("Players: " + resultTablePlot.getInt("player_count"));

                        builder.addField(StringUtil.stripColorCodes(resultTablePlot.getString("name")) +
                                        String.format(" **(%s)**", resultTablePlot.getInt("id")),
                                String.join("\n", stats), false);
                    } while (resultTablePlot.next());

                })
                .onNotFound(() -> {
                    builder.setTitle("Error!");
                    builder.setDescription("Player was not found");
                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();


    }


}

