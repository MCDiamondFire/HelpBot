package com.owen1212055.helpbot.command.commands.query;

import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.command.arguments.DefinedStringArg;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.events.CommandEvent;

import java.util.List;
import java.util.stream.Collectors;


public class RankCommand extends AbstractMultiQueryCommand {
    @Override
    public String getName() {
        return "unlocks";
    }

    @Override
    public String getDescription() {
        return "Search actions/game values by how they are unlocked.";
    }

    @Override
    public DefinedStringArg getArgument() {
        return new DefinedStringArg(new String[]{
                "Noble", "Emperor", "Mythic", "Overlord", "Credits"
        });
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }


    @Override
    protected List<String> filterData(List<SimpleData> data, CommandEvent event) {
        String closestArg = getArgument().getClosestOption(event.getParsedArgs());
        if (closestArg.equals("Credits")) {
            return data.stream()
                    .filter((action) -> action.getItem().getRequiredCredits())
                    .map(SimpleData::getMainName)
                    .collect(Collectors.toList());
        }

        return data.stream()
                .filter((simpleData -> simpleData.getItem().getRequiredRank().equalsIgnoreCase(closestArg)))
                .map(SimpleData::getMainName)
                .collect(Collectors.toList());
    }

    @Override
    protected String getSearchQuery(CommandEvent event) {
        return getArgument().getClosestOption(event.getParsedArgs());
    }
}
