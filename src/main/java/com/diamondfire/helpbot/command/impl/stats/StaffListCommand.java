package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.components.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StaffListCommand extends Command {

    @Override
    public String getName() {
        return "stafflist";
    }

    @Override
    public String getDescription() {
        return "Gets current staff members.";
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        MultiSelectorBuilder builder = new MultiSelectorBuilder();
        builder.setChannel(event.getChannel().getIdLong());
        builder.setUser(event.getMember().getIdLong());

        new SingleQueryBuilder()
                .query("SELECT players.name AS name, ranks.support AS support FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support > 0 AND ranks.moderation = 0 && ranks.retirement = 0")
                .onQuery((resultTable) -> {
                    HashMap<String, Integer> supports = new HashMap<>();
                    do {
                        supports.put(resultTable.getString("name"), resultTable.getInt("support"));
                    } while (resultTable.next());
                    EmbedBuilder page = new EmbedBuilder();
                    ArrayList<String> jrHelpers = new ArrayList<>();
                    ArrayList<String> helpers = new ArrayList<>();
                    ArrayList<String> experts = new ArrayList<>();

                    for (Map.Entry<String, Integer> player : supports.entrySet()) {
                        switch (player.getValue()) {
                            case 1:
                                jrHelpers.add(player.getKey());
                                break;
                            case 2:
                                helpers.add(player.getKey());
                                break;
                            case 3:
                                experts.add(player.getKey());
                                break;
                        }
                    }

                    Util.addFields(page, experts, "Experts", "");
                    Util.addFields(page, helpers, "Helpers", "");
                    Util.addFields(page, jrHelpers, "JrHelpers", "");
                    builder.addPage("Support", page);
                }).execute();
        new SingleQueryBuilder()
                .query("SELECT players.name AS name, ranks.moderation AS mod_rank FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.moderation > 0 && ranks.retirement = 0")
                .onQuery((resultTable) -> {
                    HashMap<String, Integer> moderators = new HashMap<>();
                    do {
                        moderators.put(resultTable.getString("name"), resultTable.getInt("mod_rank"));
                    } while (resultTable.next());
                    EmbedBuilder modPage = new EmbedBuilder();
                    EmbedBuilder adminPage = new EmbedBuilder();
                    ArrayList<String> jrMods = new ArrayList<>();
                    ArrayList<String> mods = new ArrayList<>();
                    ArrayList<String> admins = new ArrayList<>();
                    ArrayList<String> owners = new ArrayList<>();

                    for (Map.Entry<String, Integer> player : moderators.entrySet()) {
                        switch (player.getValue()) {
                            case 1:
                                jrMods.add(player.getKey());
                                break;
                            case 2:
                                mods.add(player.getKey());
                                break;
                            case 3:
                                admins.add(player.getKey());
                                break;
                            case 4:
                                owners.add(player.getKey());
                                break;
                        }
                    }

                    Util.addFields(modPage, mods, "Mods", "");
                    Util.addFields(modPage, jrMods, "JrMods", "");
                    builder.addPage("Moderation", modPage);

                    Util.addFields(adminPage, owners, "Owner", "");
                    Util.addFields(adminPage, admins, "Admins", "");
                    builder.addPage("Administration", adminPage);

                }).execute();

        // Not pretty, ik. But it would require me to cache things and that is yucky.
        EmbedBuilder devEmbed = new EmbedBuilder();

        Util.addFields(devEmbed, Arrays.asList("K_Sasha", "S4nders", "RedDaedalus"), "DiamondFire Developers", "");
        Util.addFields(devEmbed, Arrays.asList("RedDaedalus", "Owen1212055", "Ottelino", "Tomoli", "EnjoyYourBan"), "Bot Developers", "");
        builder.addPage("Developers", devEmbed);

        builder.build().send();


    }


}


