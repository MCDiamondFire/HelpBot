package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.multiselector.MultiSelectorBuilder;
import net.dv8tion.jda.api.EmbedBuilder;

public class RulesCommand extends Command {
    
    @Override
    public String getName() {
        return "rules";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"supportrules", "supporteerules"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Lists rules depending on the given category.")
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        MultiSelectorBuilder builder = new MultiSelectorBuilder();
        builder.setChannel(event.getChannel().getIdLong());
        builder.setUser(event.getMember().getIdLong());
        
        EmbedBuilder rulesEmbed = new EmbedBuilder();
        rulesEmbed.addField("Minecraft Server Rules", "https://mcdiamondfire.com/rules/", true);
        rulesEmbed.addField("Discord Server Rules", "<#337376406227124235>", true);
        EmbedBuilder supportRules = new EmbedBuilder();
        supportRules.setDescription("Failing to follow these rules may result in warnings and even removal of rank in certain cases.");
        
        String[] denyRequestReasons = new String[]{
                "They believe the supportee is already capable of completing the request, and are purely using support to progress their plot.",
                "They believe the request is too far above the supportee's coding level, and they would not understand the code, or how to change/add in more code.",
                "It is a request to build on the plot",
                "The support member is unable to complete the request, in this case, the support member should ask any expert or more experienced support members."};
        supportRules.addField("A Support member has the right to deny a request in a session if", list(denyRequestReasons, "-"), false);
        supportRules.addField("Support should make sure that the supportee understands the new code before the end of the session.",
                "This knowledge can be easily gained by a simple \"Do you understand what I have done?\" and is crucial in making sure they could add or modify your code.", false);
        supportRules.addField("Support are allowed to afk for up to 10 minutes on the main nodes, with the exception of Node Beta, where afking is allowed indefinitely.",
                "", false);
        supportRules.addField("After one hour of session time, the support member may ask the supportee to end the session, so they can take a break.",
                "In this circumstance, the support member should check if anyone else is online, so they can continue the session.", false);
        supportRules.addField("If, at any point in the session, the supportee is afk for at least one minute, the support member can end the session by using /support kill. If this is regular from someone, they should be reported in the session-reports channel.",
                "", false);
        supportRules.addField("If you are in a particularly hostile session, where the supportee is being rude, ignoring what you're doing or removing your code after you place it, you should report them in #session-reports, and tell any online experts of the occurrence.",
                "", false);
        
        EmbedBuilder supporteeRules = new EmbedBuilder();
        supporteeRules.setDescription("Failing to follow these rules may result in warnings or queue bans.");
        
        String[] supporteeShouldNot = new String[]{
                "AFK in sessions (do /support end first)",
                "Request Support for something you already know how to code",
                "Leave and join the queue repeatedly.",
                "Request Support to celebrate someone getting promoted",
                "Be rude or disrespectful to the support member",
                "Request Support as a joke",
                "Request Support to help farm sessions",
                "Ignore the Support Member, or pay no attention to what they say."
        };
        supporteeRules.addField("Supportees should not:", list(supporteeShouldNot, "-"), false);
        supporteeRules.addField("After being in queue for at least 15 minutes, a supportee is allowed to respectably notify each online support about the queue once. " +
                "Continuously asking support to do the queue may result in a queue ban.", "", false);
        supporteeRules.addField("Support question",
                "You should only use support question in the hope of gaining coding advice about the DiamondFire plugin, such as code related questions, but not personal questions (No plot idea questions or joke requests)", false);
        supporteeRules.addField("If you feel like the support member is unsure of what to do, and with permission from the current support member, you may ask them to kill the session, so you can rejoin the queue and get help from another support member.",
                "", false);
        supporteeRules.addField("If the believe the support member is not helping, or they break any rules, you should report them to an expert or moderator.",
                "", false);
        
        builder.addPage("General Rules", rulesEmbed);
        builder.addPage("Support Rules", supportRules);
        builder.addPage("Supportee Rules", supporteeRules);
        
        builder.build().send(HelpBotInstance.getJda());
    }
    
    private String list(String[] strings, String prefix) {
        return prefix + " " + String.join(" \n" + prefix + " ", strings);
    }
    
}


