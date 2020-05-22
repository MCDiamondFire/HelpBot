package com.owen1212055.helpbot.command.commands.query;

import com.owen1212055.helpbot.components.codedatabase.db.CodeDatabase;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.CodeBlockActionData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.CodeBlockData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.command.arguments.DefinedStringArg;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.events.CommandEvent;

import java.util.List;
import java.util.stream.Collectors;


public class BlockCommand extends AbstractMultiQueryCommand {
    @Override
    public String getName() {
        return "block";
    }

    @Override
    public String getDescription() {
        return "Searches for actions based on their code block.";
    }

    @Override
    public DefinedStringArg getArgument() {
        return new DefinedStringArg(CodeDatabase.getCodeBlocks().stream()
                .filter((codeBlockData -> codeBlockData.getAssociatedAction() == null))
                .map(CodeBlockData::getName)
                .toArray(String[]::new));
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }


    @Override
    protected List<String> filterData(List<SimpleData> data, CommandEvent event) {
        String closestArg = getArgument().getClosestOption(event.getParsedArgs());

        return data.stream()
                .filter(simpleData -> simpleData instanceof CodeBlockActionData)
                .filter((simpleData -> ((CodeBlockActionData) simpleData).getCodeblockName().equalsIgnoreCase(closestArg)))
                .map(SimpleData::getMainName)
                .collect(Collectors.toList());
    }

    @Override
    protected String getSearchQuery(CommandEvent event) {
        return getArgument().getClosestOption(event.getParsedArgs());
    }
}
