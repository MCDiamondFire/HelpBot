package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.components.creator.CreatorLevel;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;


public class CpCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "cp";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cpi", "cpinfo", "mycp", "cplevel"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets information on a certain player's CP.")
                .category(CommandCategory.STATS)
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
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new MinecraftUserPreset(player),
                        new InformativeReply(InformativeReplyType.INFO, "CP Info", null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT * FROM creator_rankings WHERE uuid = ? OR name = ?;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery(table -> {
                    String playerName = table.getString("name");
                    String uuid = table.getString("name");
                    int points = table.getInt("points");
                    int rank = table.getInt("cur_rank");
                    CreatorLevel level = CreatorLevel.getLevel(rank);
                    CreatorLevel nextLevel = CreatorLevel.getNextLevel(CreatorLevel.getLevel(rank));
                    int nextLevelReq = nextLevel.getRequirementProvider().getRequirement();

                    embed.addField("Current Rank", level.toString(), false);
                    embed.addField("Current Points", points + "", false);
                    new SingleQueryBuilder()
                            .query("SELECT COUNT(*) + 1 AS place FROM creator_rankings WHERE points > ?", (statement) -> {
                                statement.setInt(1, points);
                            })
                            .onQuery((tableSet) -> {
                                embed.addField("Current Leaderboard Place", tableSet.getInt("place") + "", false);
                            }).execute();

                    if (level != CreatorLevel.DIAMOND) {
                        embed.addField("Next Rank", nextLevel.toString(), true);
                        embed.addField("Next Rank Points", nextLevelReq + String.format(" (%s to go)", nextLevelReq - points), false);
                    }

                })
                .onNotFound(() -> {
                    embed.clear();
                    preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found!"));
                }).execute();
        event.reply(preset);
    }

}


