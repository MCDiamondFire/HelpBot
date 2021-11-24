package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;

import java.util.*;


public class SearchCommand extends AbstractMultiQueryCommand {
    
    @Override
    public String getName() {
        return "search";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"find"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Tries to find a list of possible actions/game values based on the given arguments.")
                .category(CommandCategory.CODE_BLOCK)
                .addArgument(
                        new HelpContextArgument()
                                .name("name")
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    protected List<String> filterData(List<CodeObject> data, CommandEvent event) {
        ArrayList<String> list = new ArrayList<>();
        String args = event.getArgument("name");
        args = args.toLowerCase();
        
        for (CodeObject simpleData : data) {
            String dataName = simpleData.getName();
            String itemName = simpleData.getItem().getItemName();
            
            if (itemName.toLowerCase().contains(args) || dataName.toLowerCase().contains(args)) {
                list.add(dataName);
            }
            
        }
        return list;
    }
}
