package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.CommandCategory;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class JerePicsCommand extends Command {

    @Override
    public String getName() {
        return "jerepic";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Very hot jeremaster pictures.")
                .category(CommandCategory.OTHER);

    }

    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("Jere Pic");
        int rngIndex = (int) (Math.random() * links.length);
        embed.setImage(links[rngIndex]);
        event.getChannel().sendMessage(embed.build()).queue();

    }
    private static final String[] links = {
            "https://imgur.com/cZZHMWY.png",
            "https://imgur.com/BGcPKv8.png",
            "https://imgur.com/seRPPR2.png",
            "https://imgur.com/e0aHeLp.png",
            "https://imgur.com/m6MjDbk.png",
            "https://imgur.com/3yaNUzm.png",
            "https://cdn.discordapp.com/attachments/180793115223916544/539188769480507426/unknown.png",
            "https://cdn.discordapp.com/attachments/789266995672907818/809169563543273512/jeremaster.png",
            "https://external-content.duckduckgo.com/iu/?reload=1613077331588&u=https://mc-heads.net/body/jeremaster/180.png",
            "https://imgur.com/LFldK2p.png",
            "https://imgur.com/aEq7voY.png"
    };


}
