package com.diamondfire.helpbot.instance;

import com.diamondfire.helpbot.command.CommandHandler;
import com.diamondfire.helpbot.command.impl.*;
import com.diamondfire.helpbot.command.impl.filespitter.ParticleListCommand;
import com.diamondfire.helpbot.command.impl.filespitter.PotionListCommand;
import com.diamondfire.helpbot.command.impl.filespitter.SoundListCommand;
import com.diamondfire.helpbot.command.impl.query.BlockCommand;
import com.diamondfire.helpbot.command.impl.query.RankCommand;
import com.diamondfire.helpbot.command.impl.query.SearchCommand;
import com.diamondfire.helpbot.command.impl.query.TagsCommand;
import com.diamondfire.helpbot.command.impl.stats.InBadCommand;
import com.diamondfire.helpbot.command.impl.stats.InBadJoinCommand;
import com.diamondfire.helpbot.command.impl.stats.StatsCommand;
import com.diamondfire.helpbot.events.MessageEvent;
import com.diamondfire.helpbot.events.ReactionEvent;
import com.diamondfire.helpbot.util.BotConstants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class BotInstance {

    private static JDA jda;
    private static final CommandHandler handler = new CommandHandler();

    public static void start() throws InterruptedException, LoginException {
        handler.register(
                // query commands
                new HelpCommand(),
                new RankCommand(),
                new BlockCommand(),
                new SearchCommand(),
                new TagsCommand(),
                //listers
                new SoundListCommand(),
                new ParticleListCommand(),
                new PotionListCommand(),
                // others
                new MimicCommand(),
                new FetchDataCommand(),
                new InfoCommand(),
                new EvalCommand(),
                // statsbot
                new StatsCommand(),
                new InBadCommand(),
                new InBadJoinCommand()
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
