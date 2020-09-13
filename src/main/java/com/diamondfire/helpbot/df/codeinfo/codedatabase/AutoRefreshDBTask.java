package com.diamondfire.helpbot.df.codeinfo.codedatabase;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.other.FetchDataCommand;
import com.diamondfire.helpbot.bot.command.impl.stats.*;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.*;
import java.util.TimeZone;
import java.util.concurrent.*;

public class AutoRefreshDBTask implements Runnable {

    public static void initialize() {
        LocalDateTime now = LocalDateTime.now(TimeZone.getTimeZone("EST").toZoneId());
        LocalDateTime nextRun = now.withHour(22).withMinute(0).withSecond(0);

        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        long timeTill = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new AutoRefreshDBTask(),
                timeTill,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        TextChannel channel = HelpBotInstance.getJda().getTextChannelById(736019542882517062L);
        channel.getHistoryFromBeginning(50).queue(messageHistory -> channel.purgeMessages(messageHistory.getRetrievedHistory()));

        NewJoinGraphCommand.generateGraph("daily", 14, channel);
        PlayerJoinGraphCommand.generateGraph("daily", 14, channel);

        TextChannel botCmds = HelpBotInstance.getJda().getTextChannelById(705205549498892299L);
        new FetchDataCommand().setup(botCmds);

        PresetBuilder preset = new PresetBuilder();
        preset.withPreset(new InformativeReply(InformativeReplyType.INFO, "Credit log db has been updated"));
        botCmds.sendMessage(preset.getEmbed().build()).queue();

        new DatabaseQuery()
                .query(new BasicQuery("INSERT INTO owen.creator_rankings_log(uuid, points, `rank`, date) " +
                        "SELECT uuid, points, cur_rank, CURRENT_DATE() FROM hypercube.creator_rankings WHERE points > 4990;")).compile();

    }
}
