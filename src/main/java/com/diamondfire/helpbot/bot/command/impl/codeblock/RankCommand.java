package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.DefinedObjectArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.List;
import java.util.stream.Collectors;


public class RankCommand extends AbstractMultiQueryCommand {

    @Override
    public String getName() {
        return "unlocks";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rank", "unlock", "ranks", "required"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Search actions/game values by how they are unlocked.")
                .category(CommandCategory.CODE_BLOCK)
                .addArgument(
                        new HelpContextArgument()
                                .name("rank|credits")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("Rank",
                        new DefinedObjectArgument("Noble", "Emperor", "Mythic", "Overlord", "Credits"));
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }


    @Override
    protected List<String> filterData(List<CodeObject> data, CommandEvent event) {
        String closestArg = event.getArgument("Rank");
        if (closestArg.equals("Credits")) {
            return data.stream()
                    .filter((action) -> action.getItem().requiresCredits())
                    .map(CodeObject::getName)
                    .collect(Collectors.toList());
        }
        return data.stream()
                .filter((simpleData -> simpleData.getItem().getRequiredRank().equalsIgnoreCase(closestArg)))
                .map(CodeObject::getName)
                .collect(Collectors.toList());
    }

    @Override
    protected String getSearchQuery(CommandEvent event) {
        return event.getArgument("Rank");
    }
}
