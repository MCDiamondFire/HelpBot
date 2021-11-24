package com.diamondfire.helpbot.bot.command.impl.other.info;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class PolicyCommand extends Command {
    
    @Override
    public String getName() {
        return "policy";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"privacy"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the bot's privacy policy.")
                .category(CommandCategory.OTHER);
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
        PresetBuilder preset = new PresetBuilder()
                .withPreset(new InformativeReply(InformativeReplyType.INFO, "Privacy Policy", null));
        EmbedBuilder embed = preset.getEmbed();
        embed.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription("This privacy policy is available for everyone to comply with Discord's Bot policy. To make it simple, only key points will be listed.");
        embed.addField("Data Collection", "HelpBot does store some data on its users. However, no personal information is ever stored.", false);
        embed.addField("Where does my data come from?", "HelpBot receives most of its information from DiamondFire. HelpBot will never show sensitive data such as ip addresses. ", false);
        embed.addField("Can I get my data removed?", "If you would like your data removed from DiamondFire, you will need to contact an administrator. If you would like your data removed from HelpBot, please contact Owen1212055.", true);
        
        event.reply(preset);
    }
}
