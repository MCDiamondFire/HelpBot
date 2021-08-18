package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.ranks.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.sql.ResultSet;
import java.util.*;

public class StaffListCommand extends Command {
    
    @Override
    public String getName() {
        return "stafflist";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"staff"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current staff members.")
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        MultiSelectorBuilder builder = new MultiSelectorBuilder();
        builder.setChannel(event.getChannel().getIdLong());
        builder.setUser(event.getMember().getIdLong());
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM ranks,players\n" +
                        "                        WHERE ranks.uuid = players.uuid\n" +
                        "                        AND (ranks.support > 0 || ranks.moderation > 0 || ranks.administration > 0 || ranks.builder = 1) AND ranks.retirement = 0"))
                .compile()
                .runAsync((result) -> {
                    Map<com.diamondfire.helpbot.df.ranks.Rank, List<String>> ranks = new HashMap<>();
                    registerRank(ranks,
                            com.diamondfire.helpbot.df.ranks.Rank.JRHELPER,
                            com.diamondfire.helpbot.df.ranks.Rank.HELPER,
                            com.diamondfire.helpbot.df.ranks.Rank.EXPERT,
                            com.diamondfire.helpbot.df.ranks.Rank.JRMOD,
                            com.diamondfire.helpbot.df.ranks.Rank.MOD,
                            com.diamondfire.helpbot.df.ranks.Rank.SR_MOD,
                            com.diamondfire.helpbot.df.ranks.Rank.ADMIN,
                            com.diamondfire.helpbot.df.ranks.Rank.OWNER,
                            com.diamondfire.helpbot.df.ranks.Rank.DEVELOPER);
                    
                    for (ResultSet set : result) {
                        String name = StringUtil.display(set.getString("name"));
                        int supportNum = set.getInt("support");
                        int moderationNum = set.getInt("moderation");
                        int administrationNum = set.getInt("administration");
                        
                        if (administrationNum > 0) {
                            ranks.get(com.diamondfire.helpbot.df.ranks.Rank.fromBranch(RankBranch.ADMINISTRATION, administrationNum)).add(name);
                        }
                        if (moderationNum == 0 && supportNum > 0 && administrationNum == 0) {
                            ranks.get(com.diamondfire.helpbot.df.ranks.Rank.fromBranch(RankBranch.SUPPORT, supportNum)).add(name);
                        } else if (moderationNum > 0 && administrationNum == 0) {
                            ranks.get(com.diamondfire.helpbot.df.ranks.Rank.fromBranch(RankBranch.MODERATION, moderationNum)).add(name);
                        }
                    }
                    
                    EmbedBuilder supportPage = new EmbedBuilder();
                    EmbedUtil.addFields(supportPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.EXPERT), "", "Experts");
                    EmbedUtil.addFields(supportPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.HELPER), "", "Helpers");
                    EmbedUtil.addFields(supportPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.JRHELPER), "", "JrHelpers");
                    builder.addPage("Support", supportPage);
                    
                    EmbedBuilder modPage = new EmbedBuilder();
                    EmbedUtil.addFields(modPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.SR_MOD), "", "SrMods");
                    EmbedUtil.addFields(modPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.MOD), "", "Mods");
                    EmbedUtil.addFields(modPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.JRMOD), "", "JrMods");
                    builder.addPage("Moderation", modPage);
                    
                    EmbedBuilder adminPage = new EmbedBuilder();
                    EmbedUtil.addFields(adminPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.OWNER), "", "Owner");
                    EmbedUtil.addFields(adminPage, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.ADMIN), "", "Admins");
                    builder.addPage("Administration", adminPage);
                    
                    EmbedBuilder devEmbed = new EmbedBuilder();
                    EmbedUtil.addFields(devEmbed, ranks.get(com.diamondfire.helpbot.df.ranks.Rank.DEVELOPER), "", "DiamondFire Developers");
                    
                    Guild guild = event.getGuild();
                    Role botDev = guild.getRoleById(589238520145510400L);
                    guild.findMembers((member -> member.getRoles().contains(botDev)))
                            .onSuccess((members) -> {
                                List<String> memberNames = new ArrayList<>();
                                for (Member member : members) {
                                    if (!member.getUser().isBot()) {
                                        memberNames.add(member.getEffectiveName());
                                    }
                                }
                                
                                EmbedUtil.addFields(devEmbed, memberNames, "", "Bot Contributors");
                                guild.pruneMemberCache();
                                builder.addPage("Developers", devEmbed);
                                builder.build().send(event.getJDA());
                            });
                });
        
    }
    
    private void registerRank(Map<com.diamondfire.helpbot.df.ranks.Rank, List<String>> map, com.diamondfire.helpbot.df.ranks.Rank... ranks) {
        for (com.diamondfire.helpbot.df.ranks.Rank rank : ranks) {
            map.put(rank, new ArrayList<>());
        }
    }
    
}


