package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;

public class PlotsCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "plots";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"ownedplots", "plotlist"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current plots owned by a certain user. (Max of 25)")
                .category(CommandCategory.PLAYER_STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Owned Plots", null),
                        new MinecraftUserPreset(player)
                );
        
        EmbedBuilder embed = preset.getEmbed();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM plots WHERE owner = ? LIMIT 25;", (statement) -> {
                    statement.setString(1, player.uuidString());
                }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player does not have any plots!"));
                        return;
                    }
                    
                    for (ResultSet plot : result) {
                        String[] stats = {
                                "Votes: " + plot.getInt("votes"),
                                "Players: " + plot.getInt("player_count")
                        };
                        embed.addField(StringUtil.display(plot.getString("name")) +
                                        String.format(" **(%s)**", plot.getInt("id")),
                                String.join("\n", stats), false);
                    
                    }
                });
    
        event.reply(preset);
    }
    
}

