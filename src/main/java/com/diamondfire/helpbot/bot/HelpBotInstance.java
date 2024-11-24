package com.diamondfire.helpbot.bot;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.impl.codeblock.*;
import com.diamondfire.helpbot.bot.command.impl.other.StoreCommand;
import com.diamondfire.helpbot.bot.command.impl.other.dev.*;
import com.diamondfire.helpbot.bot.command.impl.other.dumps.*;
import com.diamondfire.helpbot.bot.command.impl.other.fun.*;
import com.diamondfire.helpbot.bot.command.impl.other.info.*;
import com.diamondfire.helpbot.bot.command.impl.other.mod.*;
import com.diamondfire.helpbot.bot.command.impl.other.tag.TagCommand;
import com.diamondfire.helpbot.bot.command.impl.other.util.*;
import com.diamondfire.helpbot.bot.command.impl.stats.*;
import com.diamondfire.helpbot.bot.command.impl.stats.graph.*;
import com.diamondfire.helpbot.bot.command.impl.stats.individualized.*;
import com.diamondfire.helpbot.bot.command.impl.stats.metrics.*;
import com.diamondfire.helpbot.bot.command.impl.stats.plot.*;
import com.diamondfire.helpbot.bot.command.impl.stats.support.*;
import com.diamondfire.helpbot.bot.command.impl.stats.top.*;
import com.diamondfire.helpbot.bot.config.Config;
import com.diamondfire.helpbot.bot.events.*;
import com.diamondfire.helpbot.sys.tasks.TaskRegistry;
import com.google.gson.Gson;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;

public class HelpBotInstance {
    
    public static final Gson GSON = new Gson();
    
    private static final Config config = new Config();
    public static final long DF_GUILD = config.getGuild();
    public static final long LOG_CHANNEL = config.getLogChannel();
    
    private static JDA jda;
    private static final TaskRegistry loop = new TaskRegistry();
    
    public static void initialize() throws LoginException {
        
        CommandHandler.getInstance().register(
                // codeblock commands
                new CodeCommand(),
                new RankCommand(),
                new BlockCommand(),
                new SearchCommand(),
                new TagsCommand(),
                // others
                //new CowsayCommand(),
                new MimicCommand(),
                new SolvedCommand(),
                //new FetchDataCommand(),
                new InfoCommand(),
                new EvalCommand(),
                //new GarfieldCommand(),
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
                new PermUnlocksCommand(),
                new MuteCommand(),
                new MutedCommand(),
                new UnmuteCommand(),
                new VerifyCommand(),
                // new PollCommand(), - Unused
                new IdeaCommand(),
                // new StoreCommand(),
                // new ChannelMuteCommand(), - not finished
                // statsbot
                new StatsCommand(),
                new SupportBadCommand(),
                new JoinBadCommand(),
                new PlotCommand(),
                new ProfileCommand(),
                new ActivePlotsCommand(),
                new TrendingPlotsCommand(),
                new PlotsCommand(),
                //new CpTopCommand(),
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
                // new NamesCommand(), - Dead
                new PlayerJoinGraphCommand(),
                //new CpCommand(),
                //new CpRequirementsCommand(),
                new VoteGivenLeaderboard(),
                new PlotVoteGraphCommand(),
                new JoinDataCommand(),
                new TotalStatsCommand(),
                new TokenTopCommand(),
                new HistoryCommand(),
                new UuidCommand(),
                new PlotLocCommand(),
                new FindSupporteeNamesCommand(),
                new SessionsCommand(),
                new SupporteeSessionsCommand(),
                new ExcuseStaffCommand(),
                new ExcusedStaffCommand(),
                new SupportBannedPlayersCommand(),
                new NbsCommand(),
                new DailySessionsCommand(),
                new EightBallCommand(),
                // new OcrCommand(), - Dead
                new JoinsCommand(),
                new TagCommand(),
                new PurgeCommand()
        );
        
        JDABuilder builder = JDABuilder.createDefault(config.getToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .setStatus(OnlineStatus.ONLINE)
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .setActivity(Activity.watching("for " + getConfig().getPrefix() + "help"))
                .setGatewayEncoding(GatewayEncoding.ETF)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
                .addEventListeners(new MessageEvent(), new ReadyEvent(), new GuildJoinEvent(), new ButtonEvent(), new MessageEditEvent(), new PostAppliedTagsEvent(), new ChannelCreatedEvent(), new ChannelArchiveEvent());
        
        jda = builder.build();
        CommandHandler.getInstance().initialize();
    }
    
    public static JDA getJda() {
        return jda;
    }
    
    public static Config getConfig() {
        return config;
    }
    
    public static TaskRegistry getScheduler() {
        return loop;
    }
}
