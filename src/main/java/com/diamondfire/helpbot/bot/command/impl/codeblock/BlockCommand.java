package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.DefinedObjectArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;

import java.util.*;


public class BlockCommand extends AbstractMultiQueryCommand {

    @Override
    public String getName() {
        return "block";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"codeblock"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Searches for actions based on their code block.")
                .category(CommandCategory.CODE_BLOCK)
                .addArgument(
                        new HelpContextArgument()
                                .name("codeblock")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        List<CodeBlockData> codeBlocks = CodeDatabase.getRegistry(CodeDatabase.CODEBLOCKS);
        List<String> strings = new ArrayList<>();

        for (CodeBlockData codeBlock : codeBlocks) {
            if (codeBlock.getAssociatedAction() == null) {
                strings.add(codeBlock.getName());
            }
        }

        return new ArgumentSet().
                addArgument("codeblock",
                        new DefinedObjectArgument<>(strings.toArray(new String[0]))
                );
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected List<String> filterData(List<CodeObject> data, CommandEvent event) {
        List<String> filteredObjects = new ArrayList<>();
        for (CodeObject object : data) {
            if (object instanceof ActionData) {
                if (((ActionData) object).getCodeblockName().equals(event.getArgument("codeblock"))) {
                    filteredObjects.add(object.getName());
                }
            }
        }

        return filteredObjects;
    }

    @Override
    protected String getSearchQuery(CommandEvent event) {
        return event.getArgument("codeblock");
    }
}
