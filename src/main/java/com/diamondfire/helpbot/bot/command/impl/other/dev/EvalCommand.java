package com.diamondfire.helpbot.bot.command.impl.other.dev;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MessageArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.EmbedUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.script.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


public class EvalCommand extends Command {
    
    private static final List<String> IMPORTS = List.of(
            "net.dv8tion.jda.core",
            "net.dv8tion.jda.core.managers",
            "net.dv8tion.jda.core.entities",
            "net.dv8tion.jda.core.entities.impl",
            "net.dv8tion.jda.core.utils");
    
    @Override
    public String getName() {
        return "eval";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Executes given code.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("code")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("code",
                        new MessageArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }
    
    @Override
    public void run(CommandEvent event) {
        String code = event.getArgument("code");
        
        // Red is a bad boy, sometimes he decides he wants to open 500 tabs on my computer! This is here to stop Red, nothing else.
        if (HelpBotInstance.getConfig().isDevBot()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("No.");
            builder.setColor(Color.red);

            event.getChannel().sendMessageEmbeds(builder.build()).queue();
            return;
        }
        
        
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
        engine.put("jda", event.getJDA());
        engine.put("event", event);
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.addField("Code", String.format("```js\n%s```", code), true);
        
        code = IMPORTS.stream().map(s -> "import " + s + ".*;").collect(Collectors.joining(" ")) + " " + code;
        
        try {
            Object object = engine.eval(code); // Returns an object of the eval
            
            builder.setTitle("Eval Result");
            builder.addField("Object Returned:", String.format("```js\n%s```", EmbedUtil.fieldSafe(object)), false);
            event.getChannel().sendMessageEmbeds(builder.build()).queue();
            
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String sStackTrace = sw.toString();
            
            builder.setTitle("Eval failed!");
            event.getChannel().sendMessageEmbeds(builder.build()).queue();
            event.getChannel().sendMessage(String.format("```%s```", sStackTrace.length() >= 1500 ? sStackTrace.substring(0, 1500) : sStackTrace)).queue();
        }
        
    }
    
}
