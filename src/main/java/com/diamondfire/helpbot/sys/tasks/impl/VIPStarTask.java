package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.tasks.LoopingTask;
import com.diamondfire.helpbot.util.StarUtil;
import net.dv8tion.jda.api.entities.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VIPStarTask implements LoopingTask {
    
    // todo: add this
    private static final long VIP_PASS_HOLDER_ROLE = 0L;
    
    @Override
    public long getInitialStart() {
        return 0;
    }
    
    @Override
    public long getNextLoop() {
        return TimeUnit.MINUTES.toMillis(30L);
    }
    
    @Override
    public void run() {
        Guild guild = HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD);
        
        if (guild == null) {
            return;
        }
        
        Role generalRole = guild.getRoleById(VIP_PASS_HOLDER_ROLE);
        
        if (generalRole == null) {
            return;
        }
        
        // Load members and create the color -> role map.
        final Map<Integer, Role> roles = new HashMap<>();
        List<Member> members = guild.loadMembers().get();
        
        
        // Load roles from the database
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM owen.vip_roles"))
                .compile()
                .run(result -> {
                    ResultSet set = result.getResult();
                    while (set.next()) {
                        roles.put(set.getInt("color"), guild.getRoleById(set.getLong("role_id")));
                    }
                });
        
        
        Set<Long> vips = new HashSet<>();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT linked_accounts.discord_id  FROM linked_accounts, hypercube.ranks " +
                        "WHERE linked_accounts.player_uuid = ranks.uuid AND ranks.vip = 1"))
                .compile()
                .run(result -> {
                    ResultSet set = result.getResult();
                    while (set.next()) {
                        vips.add(set.getLong("discord_id"));
                    }
                });
        
        for (Member member : members) {
            Role role = roles.get(member.getColorRaw());
            if (role == null) {
                try {
                    // Create the role and add it to the roles map.
                    role = createRole(guild, member.getColor(), member.getColorRaw());
                    roles.put(member.getColorRaw(), role);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // If the vips returned by the db contains the member add the role.
            if (vips.contains(member.getIdLong())) {
                if (!member.getRoles().contains(role)) {
                    guild.addRoleToMember(member, role).queue();
                }
                if (!member.getRoles().contains(generalRole)) {
                    guild.addRoleToMember(member, generalRole).queue();
                }
            } else {
                // If the user has the roles, remove them.
                if (member.getRoles().contains(role)) {
                    guild.removeRoleFromMember(member, role).queue();
                }
                if (member.getRoles().contains(generalRole)) {
                    guild.removeRoleFromMember(member, generalRole).queue();
                }
            }
        }
    }
    
    /**
     * Creates a colored star role and adds it to the database.
     */
    private Role createRole(Guild guild, Color color, int colorRaw) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(StarUtil.create(color), "png", baos);
        baos.flush();
        Role role = guild.createRole()
                .setName(" ")
                .setIcon(Icon.from(baos.toByteArray()))
                .setPermissions(0L)
                .complete();
        new DatabaseQuery()
                .query(new BasicQuery("INSERT INTO owen.vip_roles (color, role_id) VALUES (?, ?)", statement -> {
                    statement.setInt(1, colorRaw);
                    statement.setLong(2, role.getIdLong());
                }))
                .compile()
                .run(ignored -> {
                });
        return role;
    }
    
}
