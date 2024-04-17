package com.diamondfire.helpbot.bot.command.impl.codeblock;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import com.diamondfire.helpbot.util.StringUtil;
import com.google.gson.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.function.BiConsumer;


public class RawCommand extends AbstractSingleQueryCommand {
    
    private static void sendRawMessage(CodeObject data, TextChannel channel) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data.getJson());
        
        for (String part : StringUtil.splitBy(json, 1950)) {
            channel.sendMessage(String.format("```%s```", part)).queue();
        }
        
    }
    
    @Override
    public String getName() {
        return "rawcode";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"coderaw"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets raw data of a codeblock/action/game value. (Anything within simpledata class)")
                .category(CommandCategory.CODE_BLOCK)
                .addArgument(
                        new HelpContextArgument()
                                .name("codeblock|action|game value")
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        super.run(event);
    }
    
    @Override
    public BiConsumer<CodeObject, TextChannel> onDataReceived() {
        return RawCommand::sendRawMessage;
    }
    
}
