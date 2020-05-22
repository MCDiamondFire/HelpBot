package com.owen1212055.helpbot.instance;

import com.owen1212055.helpbot.command.CommandHandler;
import com.owen1212055.helpbot.command.commands.*;
import com.owen1212055.helpbot.command.commands.filespitter.ParticleListCommand;
import com.owen1212055.helpbot.command.commands.filespitter.PotionListCommand;
import com.owen1212055.helpbot.command.commands.filespitter.SoundListCommand;
import com.owen1212055.helpbot.command.commands.query.*;
import com.owen1212055.helpbot.command.commands.stats.InBadCommand;
import com.owen1212055.helpbot.command.commands.stats.InBadJoinCommand;
import com.owen1212055.helpbot.command.commands.stats.StatsCommand;
import com.owen1212055.helpbot.events.MessageEvent;
import com.owen1212055.helpbot.events.ReactionEvent;
import com.owen1212055.helpbot.util.BotConstants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class BotInstance {
    private static JDA jda;
    private static CommandHandler handler = new CommandHandler();

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
