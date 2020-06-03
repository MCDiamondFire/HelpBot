package com.diamondfire.helpbot.command.impl.query;

import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.events.CommandEvent;

import java.util.ArrayList;
import java.util.List;


public class SearchCommand extends AbstractMultiQueryCommand {

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "Tries to find a list of possible actions/game values based on the given arguments.";
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected List<String> filterData(List<SimpleData> data, CommandEvent event) {
        ArrayList<String> list = new ArrayList<>();
        String args = event.getParsedArgs().toLowerCase();

        for (SimpleData simpleData : data) {
            if (simpleData.getItem().getItemName().toLowerCase().contains(args) || simpleData.getMainName().toLowerCase().contains(args)) {
                list.add(simpleData.getMainName());
            }

        }
        return list;
    }
}
