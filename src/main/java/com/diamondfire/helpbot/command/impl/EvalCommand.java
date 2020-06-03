package com.diamondfire.helpbot.command.impl;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringFormatting;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.PrintWriter;
import java.io.StringWriter;


public class EvalCommand extends Command {

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getDescription() {
        return "Executes given code..";
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
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");

        engine.put("jda", event.getJDA());
        engine.put("event", event);


        String code = event.getParsedArgs().replaceAll("([^(]+?)\\s*->", "function($1)");
        EmbedBuilder builder = new EmbedBuilder();
        builder.addField("Code", String.format("```js\n%s```", code), true);

        try {
            Object object = engine.eval(code); // Returns an object of the eval

            builder.setTitle("Eval Result");
            builder.addField("Object Returned:", String.format("```js\n%s```", StringFormatting.fieldSafe(object)), false);
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
