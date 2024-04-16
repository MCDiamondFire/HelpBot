package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.punishments.*;
import com.diamondfire.helpbot.df.punishments.fetcher.PunishmentFetcher;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;


public class HistoryCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "history";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"punishments", "warns", "warninglist", "punishmentlist", "bans", "kicks", "mutes", "banlist", "warnings"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Sends you your punishment history.")
                .category(CommandCategory.PLAYER_STATS)
                .addArgument(new HelpContextArgument()
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
        if (!Permission.MODERATION.hasPermission(event.getMember()) && !event.getMember().getEffectiveName().equals(player.name())) {
            PresetBuilder activePunishmentsPreset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.ERROR, "You must be a moderator inorder to see other peoples history!")
                    );
            
            event.reply(activePunishmentsPreset);
            
            return;
        }
        
        List<MessageCreateAction> msgs = new ArrayList<>();
        List<Punishment> punishments = new PunishmentFetcher()
                .withUUID(player.uuidString())
                .withAll()
                .fetch();
        
        event.getMember().getUser().openPrivateChannel().queue((privateChannel) -> {
            // Retrieve active punishments
            {
                PresetBuilder activePunishmentsPreset = new PresetBuilder()
                        .withPreset(
                                new MinecraftUserPreset(player),
                                new InformativeReply(InformativeReplyType.INFO, "History Recap", null)
                        );
                EmbedBuilder embed = activePunishmentsPreset.getEmbed();
                
                List<String> activePunishments = new ArrayList<>();
                int warnings = 0;
                int yearlyWarnings = 0;
                for (Punishment punishment : punishments) {
                    if (punishment.active) {
                        activePunishments.add(punishment.toString());
                    }
                    if (punishment.type == PunishmentType.WARN) {
                        if (ChronoUnit.DAYS.between(punishment.startTime.toInstant(), Instant.now()) <= 365) {
                            yearlyWarnings++;
                        }
                        warnings++;
                    }
                }
                punishments.removeIf((punishment) -> punishment.active);
                
                if (activePunishments.size() > 0) {
                    activePunishmentsPreset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Active Punishments", null));
                    EmbedUtil.addFields(embed, activePunishments, "", true);
                    
                    String duration = null;
                    int warningReq = 0;
                    if (warnings < 4 && warnings > 1) {
                        duration = "30 minute";
                        warningReq = 4 - warnings;
                    } else if (warnings < 5) {
                        duration = "1 hour";
                        warningReq = 5 - warnings;
                    } else if (warnings < 6) {
                        duration = "12 hour";
                        warningReq = 6 - warnings;
                    }
                    
//                    if (duration != null) {
//                        embed.addField("Tempban", String.format("\u26A0 If you receive **%s** more active %s, you will receive a **%s** tempban!", warningReq, StringUtil.sCheck("warning", warningReq), duration), false);
//                    }
                    
                } else if (punishments.size() == 0) {
                    embed.setDescription("No punishments here, keep up the good work!");
                }
//
//                if (yearlyWarnings > 10) {
//                    embed.setColor(Color.RED);
//                    embed.addField("Tempban", String.format("\u26A0 If you receive **%s** more %s this year, you will receive a **45** day tempban!", 20 - yearlyWarnings, StringUtil.sCheck("warning", 20 - yearlyWarnings)), false);
//                }
                
                msgs.add(privateChannel.sendMessageEmbeds(embed.build()));
            }
            
            // Retrieve normal punishments
            {
                PresetBuilder punishmentsPreset = new PresetBuilder()
                        .withPreset(
                                new InformativeReply(InformativeReplyType.INFO, "Punishment History", null)
                        );
                EmbedBuilder presetBuilder = punishmentsPreset.getEmbed();
                List<String> punishmentStrings = new ArrayList<>();
                for (Punishment punishment : punishments) {
                    punishmentStrings.add(punishment.toString());
                }
                
                EmbedUtil.addFields(presetBuilder, punishmentStrings, "", "", true);
                if (punishmentStrings.size() == 0) {
                } else if (presetBuilder.isValidLength()) {
                    msgs.add(privateChannel.sendMessageEmbeds(presetBuilder.build()));
                } else {
                    try {
                        File sendFile = ExternalFileUtil.generateFile("history.txt");
                        Files.writeString(sendFile.toPath(), String.join("\n", punishmentStrings));
                        msgs.add(privateChannel.sendFiles(FileUpload.fromData(sendFile)));
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                
            }
            
            MessageCreateAction action = msgs.get(0);
            msgs.remove(0);
            
            action.queue((msg) -> {
                PresetBuilder successMSG = new PresetBuilder()
                        .withPreset(
                                new InformativeReply(InformativeReplyType.SUCCESS, "Check your messages for history!"),
                                new MinecraftUserPreset(player)
                        );
                
                event.reply(successMSG);
                
                for (MessageCreateAction msgAction : msgs) {
                    msgAction.queue();
                }
            }, (error) -> {
                PresetBuilder errorMSG = new PresetBuilder()
                        .withPreset(new InformativeReply(InformativeReplyType.ERROR, "Could not send private message! Please check to make sure private messages are enabled."));
                event.reply(errorMSG);
            });
            
        });
    }
    
}
