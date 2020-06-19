package com.diamondfire.helpbot.instance;

import com.diamondfire.helpbot.command.CommandHandler;
import com.diamondfire.helpbot.command.impl.filespitter.ParticleListCommand;
import com.diamondfire.helpbot.command.impl.filespitter.PotionListCommand;
import com.diamondfire.helpbot.command.impl.filespitter.SoundListCommand;
import com.diamondfire.helpbot.command.impl.other.*;
import com.diamondfire.helpbot.command.impl.query.*;
import com.diamondfire.helpbot.command.impl.stats.*;
import com.diamondfire.helpbot.events.MessageEvent;
import com.diamondfire.helpbot.events.ReactionEvent;
import com.diamondfire.helpbot.util.BotConstants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class BotInstance {

    private static final CommandHandler handler = new CommandHandler();
    private static JDA jda;

    public static void start() throws InterruptedException, LoginException {
        handler.register(
                // query commands
                new CodeCommand(),
                new RankCommand(),
                new BlockCommand(),
                new SearchCommand(),
                new TagsCommand(),
                //file listers
                new SoundListCommand(),
                new ParticleListCommand(),
                new PotionListCommand(),
                // others
                new MimicCommand(),
                new FetchDataCommand(),
                new InfoCommand(),
                new EvalCommand(),
                new GarfieldCommand(),
                new HelpCommand(),
                // statsbot
                new StatsCommand(),
                new InBadCommand(),
                new JoinBadCommand(),
                new PlotCommand(),
                new ProfileCommand(),
                new ActivePlotsCommand(),
                new TrendingPlotsCommand(),
                new PlotsCommand(),
                new CpTopCommand(),
                new SamQuotesCommand(),
                new RetiredListCommand(),
                new StaffListCommand(),
                new SessionTopCommand(),
                new LastJoinedCommand(),
                new SessionStatsCommand(),
                new NewPlayersCommand()
        );

        JDABuilder builder = JDABuilder.createDefault(BotConstants.TOKEN);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("for ?help"));
        builder.addEventListeners(new MessageEvent(), new ReactionEvent());

        jda = builder.build();
        jda.awaitReady();
    }

    public static JDA getJda() {
        return jda;
    }

    public static CommandHandler getHandler() {
        return handler;
    }


}
