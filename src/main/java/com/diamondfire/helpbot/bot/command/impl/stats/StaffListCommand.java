package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.bot.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.df.ranks.Ranks;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

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
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder devEmbed = new EmbedBuilder();
        MultiSelectorBuilder builder = new MultiSelectorBuilder();
        builder.setChannel(event.getChannel().getIdLong());
        builder.setUser(event.getMember().getIdLong());
        new SingleQueryBuilder()
                .query("SELECT * FROM ranks, players " +
                        "WHERE ranks.uuid = players.uuid " +
                        "AND (ranks.support > 0 || ranks.moderation > 0 || ranks.developer = 1 || ranks.builder = 1) AND ranks.retirement = 0")
                .onQuery((resultTable) -> {
                    EmbedBuilder helperPage = new EmbedBuilder();
                    EmbedBuilder modPage = new EmbedBuilder();
                    EmbedBuilder adminPage = new EmbedBuilder();
                    Map<Integer, List<String>> support = new HashMap<>();
                    Map<Integer, List<String>> moderation = new HashMap<>();
                    List<String> devs = new ArrayList<>();


                    support.put(Ranks.JRHELPER.getNumber(), new ArrayList<>());
                    support.put(Ranks.HELPER.getNumber(), new ArrayList<>());
                    support.put(Ranks.EXPERT.getNumber(), new ArrayList<>());
                    moderation.put(Ranks.JRMOD.getNumber(), new ArrayList<>());
                    moderation.put(Ranks.MOD.getNumber(), new ArrayList<>());
                    moderation.put(Ranks.ADMIN.getNumber(), new ArrayList<>());
                    moderation.put(Ranks.OWNER.getNumber(), new ArrayList<>());

                    do {
                        String name = StringUtil.display(resultTable.getString("name"));
                        int supportNum = resultTable.getInt("support");
                        int moderationNum = resultTable.getInt("moderation");
                        int developerNum = resultTable.getInt("developer");

                        if (developerNum != 0) {
                            devs.add(name);
                        }
                        if (moderationNum == 0 && supportNum > 0) {
                            support.get(supportNum).add(name);
                        } else if (moderationNum > 0) {
                            moderation.get(moderationNum).add(name);
                        }

                    } while (resultTable.next());

                    Util.addFields(helperPage, support.get(Ranks.EXPERT.getNumber()), "", "Experts");
                    Util.addFields(helperPage, support.get(Ranks.HELPER.getNumber()), "", "Helpers");
                    Util.addFields(helperPage, support.get(Ranks.JRHELPER.getNumber()), "", "JrHelpers");
                    builder.addPage("Support", helperPage);

                    Util.addFields(modPage, moderation.get(Ranks.MOD.getNumber()), "", "Mods");
                    Util.addFields(modPage, moderation.get(Ranks.JRMOD.getNumber()), "", "JrMods");
                    builder.addPage("Moderation", modPage);

                    Util.addFields(adminPage, moderation.get(Ranks.OWNER.getNumber()), "", "Owner");
                    Util.addFields(adminPage, moderation.get(Ranks.ADMIN.getNumber()), "", "Admins");
                    builder.addPage("Administration", adminPage);

                    Util.addFields(devEmbed, devs, "", "DiamondFire Developers");
                }).execute();

        Guild guild = event.getGuild();
        Role botDev = guild.getRoleById(589238520145510400L);

        guild.findMembers((member -> member.getRoles().contains(botDev))).onSuccess((members) -> {
            List<String> memberNames = new ArrayList<>();
            for (Member member : members) {
                if (!member.getUser().isBot()) {
                    memberNames.add(member.getEffectiveName());
                }
            }

            Util.addFields(devEmbed, memberNames, "", "Bot Developers");
            guild.pruneMemberCache();
            builder.addPage("Developers", devEmbed);
            builder.build().send(event.getJDA());
        });
    }

}


