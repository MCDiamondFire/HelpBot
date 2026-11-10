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
import net.dv8tion.jda.api.entities.*;
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
        return new String[]{"punishments", "warns", "warninglist", "punishmentlist", "bans", "kicks", "mutes", "banlist", "warnings", "hist"};
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
        // No player argument means the member's own (linked) account was used.
        boolean self = event.getArgument("player") == null || event.getMember().getEffectiveName().equalsIgnoreCase(player.name());
        if (!self && !Permission.MODERATION.hasPermission(event.getMember())) {
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
            // Recap: summary counts and any active punishments.
            {
                List<Punishment> active = punishments.stream().filter((punishment) -> punishment.active).toList();
                
                PresetBuilder recapPreset = new PresetBuilder()
                        .withPreset(
                                new MinecraftUserPreset(player),
                                active.isEmpty()
                                        ? new InformativeReply(InformativeReplyType.INFO, "History Recap", null)
                                        : new InformativeReply(InformativeReplyType.ERROR, "Active Punishments", null)
                        );
                EmbedBuilder embed = recapPreset.getEmbed();
                
                if (punishments.isEmpty()) {
                    embed.setDescription("No punishments here, keep up the good work!");
                } else {
                    StringBuilder description = new StringBuilder(summarize(punishments));
                    for (Punishment punishment : active) {
                        description.append("\n\n").append(punishment.format());
                    }
                    
                    embed.setDescription(description.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH
                            ? description.substring(0, MessageEmbed.DESCRIPTION_MAX_LENGTH - 1) + "…"
                            : description.toString());
                }
                
                msgs.add(privateChannel.sendMessageEmbeds(embed.build()));
                punishments.removeIf((punishment) -> punishment.active);
            }
            
            // Past punishments, newest first, split over as many embeds as needed.
            if (!punishments.isEmpty()) {
                punishments.sort(Comparator.comparing((Punishment punishment) -> punishment.startTime).reversed());
                
                List<MessageEmbed> embeds = new ArrayList<>();
                StringBuilder page = new StringBuilder();
                int total = 0;
                for (Punishment punishment : punishments) {
                    String entry = punishment.format();
                    if (page.length() + entry.length() + 2 > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
                        embeds.add(historyEmbed(page.toString(), embeds.isEmpty()));
                        page.setLength(0);
                    }
                    
                    page.append(page.isEmpty() ? "" : "\n\n").append(entry);
                    total += entry.length() + 2;
                }
                embeds.add(historyEmbed(page.toString(), embeds.isEmpty()));
                
                if (embeds.size() <= Message.MAX_EMBED_COUNT && total < MessageEmbed.EMBED_MAX_LENGTH_BOT - 100) {
                    msgs.add(privateChannel.sendMessageEmbeds(embeds));
                } else {
                    try {
                        File sendFile = ExternalFileUtil.generateFile("history.txt");
                        Files.writeString(sendFile.toPath(), String.join("\n\n", punishments.stream().map(Punishment::toString).toList()));
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
            
        }, error -> {
            error.printStackTrace();
            event.reply(new PresetBuilder().withPreset(new InformativeReply(InformativeReplyType.ERROR,
                    "Could not open a private message. Please check that private messages are enabled.")));
        });
    }
    
    private static MessageEmbed historyEmbed(String description, boolean first) {
        EmbedBuilder embed = new PresetBuilder()
                .withPreset(new InformativeReply(InformativeReplyType.INFO, first ? "Punishment History" : null, null))
                .getEmbed();
        return embed.setDescription(description).build();
    }
    
    private static String summarize(List<Punishment> punishments) {
        Map<PunishmentType, Integer> counts = new EnumMap<>(PunishmentType.class);
        int yearlyWarnings = 0;
        for (Punishment punishment : punishments) {
            counts.merge(punishment.type, 1, Integer::sum);
            if (punishment.type == PunishmentType.WARN && ChronoUnit.DAYS.between(punishment.startTime.toInstant(), Instant.now()) <= 365) {
                yearlyWarnings++;
            }
        }
        
        int warnings = counts.getOrDefault(PunishmentType.WARN, 0);
        return String.format("**%s** %s (%s in the past year) · **%s** %s · **%s** %s · **%s** %s",
                warnings, StringUtil.sCheck("warning", warnings), yearlyWarnings,
                counts.getOrDefault(PunishmentType.MUTE, 0), StringUtil.sCheck("mute", counts.getOrDefault(PunishmentType.MUTE, 0)),
                counts.getOrDefault(PunishmentType.KICK, 0), StringUtil.sCheck("kick", counts.getOrDefault(PunishmentType.KICK, 0)),
                counts.getOrDefault(PunishmentType.BAN, 0), StringUtil.sCheck("ban", counts.getOrDefault(PunishmentType.BAN, 0)));
    }
    
}
