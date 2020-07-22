package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.changelog.CodeDifferenceHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.CodeDatabase;
import com.diamondfire.helpbot.sys.externalfile.ExternalFile;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.LinkedHashMap;

public class PolicyCommand extends Command {

    @Override
    public String getName() {
        return "policy";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the bot's privacy policy.")
                .category(CommandCategory.OTHER);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(new InformativeReply(InformativeReplyType.INFO, "Privacy Policy", null));
        EmbedBuilder embed = preset.getEmbed();
        embed.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription("This privacy policy is available for everyone to comply with Discord's Bot policy. To make it simple, only key points will be listed.");
        embed.addField("Data Collection", "HelpBot does not store any data from its users.", false);
        embed.addField("Where does my data come from?", "HelpBot receives information from DiamondFire. Sensitive data on a user is not provided by the bot, only public information that is typically available to everyone on DiamondFire already.", false);
        embed.addField("Can I get my data removed?", "As stated before, all data is provided by DiamondFire. If you would like your data removed from DiamondFire, you will need to contact an administrator.", true);


        event.reply(preset);
    }
}
