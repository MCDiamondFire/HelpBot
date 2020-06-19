package com.diamondfire.helpbot.command.impl.query;

import com.diamondfire.helpbot.command.arguments.value.DefinedStringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.events.CommandEvent;

import java.util.List;
import java.util.stream.Collectors;


public class RankCommand extends AbstractMultiQueryCommand {

    @Override
    public String getName() {
        return "unlocks";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rank"};
    }


    @Override
    public String getDescription() {
        return "Search actions/game values by how they are unlocked.";
    }

    @Override
    public ValueArgument<String> getArgument() {
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
        String closestArg = getArgument().getArg(event.getParsedArgs());

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
        return getArgument().getArg(event.getParsedArgs());
    }
}
