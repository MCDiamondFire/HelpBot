package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.punishments.Punishment;
import com.diamondfire.helpbot.df.punishments.fetcher.PunishmentFetcher;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.*;


public class HistoryCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Sends you your punishment history.")
                .category(CommandCategory.STATS);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {

        if (!Permission.MODERATION.hasPermission(event.getMember())) {
            player = event.getMember().getEffectiveName();
        }

        PresetBuilder recapPreset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "History Recap", null)
                );
        //TODO Add tempban warn info
        //30 mins = 4 warnings
        //1 hour = 5 warnings
        //12 hours = 6 warnings
        String finalPlayer = player;
        new SingleQueryBuilder()
                .query("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, finalPlayer);
                    statement.setString(2, finalPlayer);
                })
                .onQuery(table -> {
                    String playerName = table.getString("name");
                    String playerUUID = table.getString("uuid");
                    List<EmbedBuilder> embeds = new ArrayList<>();
                    List<Punishment> activePunishments = new ArrayList<>();
                    List<Punishment> punishments = new PunishmentFetcher()
                            .withUUID(playerUUID)
                            .withAll()
                            .fetch();
                    recapPreset.withPreset(new MinecraftUserPreset(playerName, playerUUID));
                    event.getMember().getUser().openPrivateChannel().queue((privateChannel) -> {
                        File sendFile = null;

                        for (Punishment punishment : punishments) {
                            if (punishment.active) {
                                activePunishments.add(punishment);
                            }
                        }
                        punishments.removeAll(activePunishments);


                        if (activePunishments.size() > 0) {
                            List<String> activePunishmentStrings = new ArrayList<>();

                            for (Punishment punishment : activePunishments) {
                                activePunishmentStrings.add(punishment.toString());
                            }

                            EmbedBuilder active = new EmbedBuilder();
                            active.setColor(Color.RED);
                            active.setTitle("\u26A0 Active Punishments");
                            active.setDescription(String.join("\n", activePunishmentStrings.toArray(new String[0])));
                            embeds.add(active);
                        }


                        if (punishments.size() > 0) {
                            List<String> punishmentStrings = new ArrayList<>();
                            for (Punishment punishment : punishments) {
                                punishmentStrings.add(punishment.toString());
                            }

                            EmbedBuilder history = new EmbedBuilder();
                            history.setTitle("All Punishments");
                            Util.addFields(history, punishmentStrings, "", "", true);
                            if (history.isValidLength()) {
                                embeds.add(history);
                            } else {
                                try {
                                    sendFile = ExternalFileUtil.generateFile("history.txt");
                                    Files.writeString(sendFile.toPath(), String.join("\n", punishmentStrings));
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }

                        File finalSendFile = sendFile;
                        event.replyA(recapPreset, privateChannel).queue((msg) -> {
                            PresetBuilder reply = new PresetBuilder();
                            reply.withPreset(
                                    new InformativeReply(InformativeReplyType.INFO, "Sent your punishment history in your private messages!")
                            );
                            event.reply(reply);

                            if (punishments.size() == 0 && activePunishments.size() == 0) {
                                // temp fix
                                msg.delete().queue();
                                recapPreset.getEmbed().clear();
                                recapPreset.withPreset(
                                        new MinecraftUserPreset(playerName, playerUUID),
                                        new InformativeReply(InformativeReplyType.INFO, "History Recap",
                                                "No punishments here, good job and keep up the good work!")
                                );
                                event.reply(recapPreset, privateChannel);
                            } else {
                                for (EmbedBuilder builder : embeds) {
                                    event.reply(builder, privateChannel);
                                }
                                if (finalSendFile != null) {
                                    privateChannel.sendFile(finalSendFile).queue();
                                }
                            }


                        }, (error) -> {
                            PresetBuilder errorMSG = new PresetBuilder();
                            errorMSG.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Could not send private message! Please check to make sure private messages are enabled."));
                            event.reply(errorMSG);

                        });

                    });
                })
                .onNotFound(() -> {
                    recapPreset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                    event.reply(recapPreset);
                }).execute();

    }

}


