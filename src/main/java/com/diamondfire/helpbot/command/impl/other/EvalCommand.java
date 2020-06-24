package com.diamondfire.helpbot.command.impl.other;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;


public class EvalCommand extends Command {

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getDescription() {
        return "Executes given code.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OTHER;
    }

    @Override
    public Argument getArgument() {
        return new StringArg("Code", true);
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {

        // Red is a bad boy, sometimes he decides he wants to open 500 tabs on my computer! This is here to stop Red, nothing else.
        if (!System.getProperty("os.name").contains("linux")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("No.");
            builder.setColor(Color.red);

            event.getChannel().sendMessage(builder.build()).queue();
            return;
        }

        ScriptEngine engine = new ScriptEngineManager().getEngineByName(args[0]);

        engine.put("jda", event.getJDA());
        engine.put("event", event);


        String code = event.getParsedArgs().replace(args[0] + " ", "").replaceAll("([^(]+?)\\s*->", "function($1)");
        EmbedBuilder builder = new EmbedBuilder();
        builder.addField("Code", String.format("```js\n%s```", code), true);

        try {
            Object object = engine.eval(code); // Returns an object of the eval

            builder.setTitle("Eval Result");
            builder.addField("Object Returned:", String.format("```js\n%s```", StringUtil.fieldSafe(object)), false);
            event.getChannel().sendMessage(builder.build()).queue();

        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String sStackTrace = sw.toString();

            builder.setTitle("Eval failed!");
            event.getChannel().sendMessage(builder.build()).queue();
            event.getChannel().sendMessage(String.format("```%s```", sStackTrace.length() >= 1500 ? sStackTrace.substring(0, 1500) : sStackTrace)).queue();

        }

    }

}
