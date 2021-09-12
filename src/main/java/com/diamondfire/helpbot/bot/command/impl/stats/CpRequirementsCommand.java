package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.creator.CreatorLevel;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;


public class CpRequirementsCommand extends Command {
    
    @Override
    public String getName() {
        return "cprequirements";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"cpreq", "cpneeded", "creatorreq"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current CP level requirements.")
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "CP Requirements", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        embed.setFooter("*Level requirement is based on a top % of players.");
        for (CreatorLevel level : CreatorLevel.values()) {
            embed.addField(level.display(), "CP Required: " + FormatUtil.formatNumber(level.getRequirementProvider().getRequirement()), true);
        }
        
        // Even it out
        embed.addBlankField(true);
        event.reply(preset);
    }
    
}


