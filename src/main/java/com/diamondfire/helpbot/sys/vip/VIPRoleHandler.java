package com.diamondfire.helpbot.sys.vip;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.util.StarUtil;
import com.google.gson.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.util.*;

public class VIPRoleHandler {

    private static final String ROLE_NAME = " ";
    
    private static final File FILE = ExternalFiles.VIP_ROLES;
    private static final Map<Integer, Long> COLOR_ROLE_MAP = new HashMap<>();

    static {
        try {
            cacheJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void cacheJson() throws IOException {
        String content = new String(Files.readAllBytes(FILE.toPath()));
        
        if (content.isEmpty()) {
            content = "{}";
        }
        
        JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
        
        for (String key : obj.keySet()) {
            long roleId = obj.get(key).getAsLong();
            COLOR_ROLE_MAP.put(Integer.parseInt(key), roleId);
        }
    }

    public static void save() throws IOException {
        JsonObject json = new JsonObject();

        for (int color : COLOR_ROLE_MAP.keySet()) {
            json.addProperty(String.valueOf(color), COLOR_ROLE_MAP.get(color));
        }

        FILE.delete();
        FILE.createNewFile();
        Files.write(FILE.toPath(), json.toString().getBytes(), StandardOpenOption.WRITE);
    }

    public static Set<Long> retrieveVIPs() {
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
        return vips;
    }

    public static Role getOrCreateRole(int color) {
        if (color == Role.DEFAULT_COLOR_RAW) {
            return null;
        }
        Guild guild = HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD);
        Role role;

        assert guild != null;

        if (COLOR_ROLE_MAP.containsKey(color)) {
            role = guild.getRoleById(COLOR_ROLE_MAP.get(color));
        } else {
            try {
                // Create the coloured star and register it on discord.
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(StarUtil.create(new Color(color)), "png", baos);
                baos.flush();
                role = guild.createRole()
                        .setName(ROLE_NAME)
                        .setIcon(Icon.from(baos.toByteArray()))
                        .setPermissions(0L)
                        .complete();
                COLOR_ROLE_MAP.put(color, role.getIdLong());
                VIPRoleHandler.save();
            } catch (IOException e) {
                return null;
            }
        }
        return role;
    }
    

}
