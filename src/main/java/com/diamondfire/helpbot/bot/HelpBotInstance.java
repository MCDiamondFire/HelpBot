package com.diamondfire.helpbot.bot;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.impl.codeblock.*;
import com.diamondfire.helpbot.bot.command.impl.other.*;
import com.diamondfire.helpbot.bot.command.impl.stats.*;
import com.diamondfire.helpbot.bot.command.impl.stats.individualized.*;
import com.diamondfire.helpbot.bot.command.impl.stats.plot.*;
import com.diamondfire.helpbot.bot.command.impl.stats.support.*;
import com.diamondfire.helpbot.bot.config.Config;
import com.diamondfire.helpbot.bot.events.*;
import com.diamondfire.helpbot.sys.tasks.TaskRegistry;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class HelpBotInstance {

    public static final long DF_GUILD = 180793115223916544L;
    public static final long LOG_CHANNEL = 762447660745359361L;
    private static final CommandHandler handler = new CommandHandler();
    private static final Config config = new Config();
    private static JDA jda;
    private static final TaskRegistry loop = new TaskRegistry();

    public static void initialize() throws LoginException {

        handler.register(
                // codeblock commands
                new CodeCommand(),
                new RankCommand(),
                new BlockCommand(),
                new SearchCommand(),
                new TagsCommand(),
                // others
                new MimicCommand(),
                new FetchDataCommand(),
                new InfoCommand(),
                new EvalCommand(),
                new GarfieldCommand(),
                new HelpCommand(),
                new RestartCommand(),
                new ActionDumpCommand(),
                new RawCommand(),
                new SamQuotesCommand(),
                new PolicyCommand(),
                new SkinCommand(),
                new DisableCommand(),
                new EnableCommand(),
                new DisableCommand(),
                new ImageDumpCommand(),
                new SoundListCommand(),
                new QueryCommand(),
                new RulesCommand(),
                new BulkExecuteCommand(),
                new SetPermissionCommand(),
                new PermUnlocksCommand(),
                new MuteCommand(),
                new MutedCommand(),
                // statsbot
                new StatsCommand(),
                new SupportBadCommand(),
                new JoinBadCommand(),
                new PlotCommand(),
                new ProfileCommand(),
                new ActivePlotsCommand(),
                new TrendingPlotsCommand(),
                new PlotsCommand(),
                new CpTopCommand(),
                new RetiredListCommand(),
                new StaffListCommand(),
                new SessionTopCommand(),
                new LastJoinedCommand(),
                new SupporteeStatsCommand(),
                new NewPlayersCommand(),
                new StatsGraphCommand(),
                new NewJoinGraphCommand(),
                new PlayersCommand(),
                new BoostersCommand(),
                new DiscordBoostersCommand(),
                new TimeTopCommand(),
                new QueueCommand(),
                new WhoHelpedCommand(),
                new HelpedByCommand(),
                new NamesCommand(),
                new PlayerJoinGraphCommand(),
                new CpCommand(),
                new CpRequirementsCommand(),
                new VoteGivenLeaderboard(),
                new PlotVoteGraphCommand(),
                new JoinDataCommand(),
                new TotalStatsCommand(),
                new CreditTopCommand(),
                new HistoryCommand(),
                new UuidCommand(),
                new PlotLocCommand(),
                new FindSupporteeNamesCommand(),
                new SessionsCommand(),
                new SupporteeSessionsCommand(),
                new ExcuseStaffCommand(),
                new ExcusedStaffCommand(),
                new SupportBannedPlayersCommand(),
                new DiscussionMuteCommand()
        );

        JDABuilder builder = JDABuilder.createDefault(config.getToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setStatus(OnlineStatus.ONLINE)
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .setActivity(Activity.watching("for ?help"))
                .setGatewayEncoding(GatewayEncoding.ETF)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
                .addEventListeners(new MessageEvent(), new ReactionEvent(), new ReadyEvent());

        jda = builder.build();
        handler.initialize();
    }

    public static JDA getJda() {
        return jda;
    }

    public static CommandHandler getHandler() {
        return handler;
    }

    public static Config getConfig() {
        return config;
    }

    public static TaskRegistry getScheduler() {
        return loop;
    }
}
