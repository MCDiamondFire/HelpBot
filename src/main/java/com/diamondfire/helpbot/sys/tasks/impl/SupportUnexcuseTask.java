package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.stats.individualized.LastJoinedCommand;
import com.diamondfire.helpbot.bot.command.impl.stats.support.StatsCommand;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.tasks.OneTimeTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.ResultSet;
import java.time.*;
import java.util.Date;

public class SupportUnexcuseTask implements OneTimeTask {
    
    private static final long EXPERT_CHAT = HelpBotInstance.getConfig().getExpertChatChannel();
//    private static final Command[] commandsToRun = new Command[]{
//            new StatsCommand(),
//            new LastJoinedCommand(),
//    };
    private final long ms;
    private final String uuid;
    private final Date initDate;
    
    public SupportUnexcuseTask(Date initDate, Date date, String uuid) {
        this.ms = Duration.between(Instant.now(), date.toInstant()).toMillis();
        this.uuid = uuid;
        this.initDate = initDate;
    }
    
    @Override
    public long getExecution() {
        return ms;
    }
    
    @Override
    public void run() {
        PresetBuilder builder = new PresetBuilder();
        TextChannel channel = HelpBotInstance.getJda().getTextChannelById(EXPERT_CHAT);
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM players WHERE players.uuid = ?", (statement) -> statement.setString(1, uuid)))
                .compile()
                .run((result) -> {
                    EmbedBuilder embed = builder.getEmbed();
                    ResultSet set = result.getResult();
                    String name = set.getString("name");
                    
                    builder.withPreset(
                            new MinecraftUserPreset(name, uuid)
                    );
                    embed.setTitle(String.format("Excuse has expired! (%s day duration)", Duration.between(initDate.toInstant(), Instant.now()).toDays()));
                    
                    channel.sendMessageEmbeds(embed.build()).queue();
                    
                    new DatabaseQuery()
                            .query(new BasicQuery("UPDATE owen.excused_staff SET handled = true WHERE uuid = ?", (statement) -> statement.setString(1, uuid)))
                            .compile();
                });
        
    }
    
    public static void prepare() {
        new DatabaseQuery()
                .query(new BasicQuery("SELECT excused_staff.uuid," +
                        "       donor," +
                        "       support," +
                        "       moderation," +
                        "       retirement," +
                        "       youtuber," +
                        "       administration," +
                        "       excused_by," +
                        "       excused_at," +
                        "       excused_till," +
                        "       reason " +
                        "FROM owen.excused_staff " +
                        "LEFT JOIN ranks r ON excused_staff.uuid = r.uuid " +
                        "WHERE (support > 0 || moderation > 0) AND handled = false " +
                        "ORDER BY excused_till DESC;"))
                .compile()
                .run((result) -> {
                    // Select unique names.
                    for (ResultSet set : result) {
                        Date dateInit = set.getTimestamp("excused_at");
                        Date date = set.getTimestamp("excused_till");
                        String uuid = set.getString("uuid");
                        
                        HelpBotInstance.getScheduler().schedule(new SupportUnexcuseTask(dateInit, date, uuid));
                    }
                });
    }
}
