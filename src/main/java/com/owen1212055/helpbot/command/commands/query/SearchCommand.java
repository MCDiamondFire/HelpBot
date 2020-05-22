package com.owen1212055.helpbot.command.commands.query;

import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.arguments.BasicStringArg;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.events.CommandEvent;

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
    public Argument getArgument() {
        return new BasicStringArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }


    @Override
    protected List<String> filterData(List<SimpleData> data, CommandEvent event) {
        ArrayList<String> list = new ArrayList<>();
        String argumentsParsed = String.join(" ", event.getArguments()).toLowerCase();
        for (SimpleData simpleData : data) {
            if (simpleData.getItem().getItemName().toLowerCase().contains(argumentsParsed) || simpleData.getMainName().toLowerCase().contains(argumentsParsed)) {
                list.add(simpleData.getMainName());
            }
        }
        return list;
    }
}
