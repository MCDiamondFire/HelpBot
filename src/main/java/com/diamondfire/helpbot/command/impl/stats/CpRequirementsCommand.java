package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.components.creator.CreatorLevel;
import com.diamondfire.helpbot.events.CommandEvent;
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
                .category(CommandCategory.STATS);
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
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "CP Requirements", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        embed.setFooter("*Level requirement is based on a top % of players.");
        for (CreatorLevel level : CreatorLevel.values()) {
            embed.addField(level.display(), "CP Required: " + level.getRequirementProvider().getRequirement(), true);
        }

        // Even it out
        embed.addBlankField(true);
        event.reply(preset);
    }

}


