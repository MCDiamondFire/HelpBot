package com.diamondfire.helpbot.command.impl.query;

import com.diamondfire.helpbot.command.arguments.value.DefinedStringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.CodeBlockActionData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.CodeBlockData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.events.CommandEvent;

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
    public ValueArgument<String> getArgument() {
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
        return data.stream()
                .filter(simpleData -> simpleData instanceof CodeBlockActionData)
                .filter((simpleData -> ((CodeBlockActionData) simpleData).getCodeblockName().equalsIgnoreCase(getArgument().getArg(event.getParsedArgs()))))
                .map(SimpleData::getMainName)
                .collect(Collectors.toList());
    }

    @Override
    protected String getSearchQuery(CommandEvent event) {
        return getArgument().getArg(event.getParsedArgs());
    }
}
