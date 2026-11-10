package com.diamondfire.helpbot.sys.vip;

import com.diamondfire.helpbot.sys.database.ConnectionProvider;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.StarUtil;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Star roles for VIPs: one role per name color, with a star icon in that color.
 * Which role belongs to which color is kept in {@code owen.vip_roles}, so it survives redeploys.
 */
public class VIPRoleHandler {

    private static final String ROLE_NAME = "VIP";

    private static final Map<Integer, Long> COLOR_ROLE_MAP = new ConcurrentHashMap<>();

    static {
        try (Connection connection = ConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS owen.vip_roles (
                        color   INT    NOT NULL PRIMARY KEY,
                        role_id BIGINT NOT NULL
                    )""");

            try (ResultSet set = statement.executeQuery("SELECT color, role_id FROM owen.vip_roles")) {
                while (set.next()) {
                    COLOR_ROLE_MAP.put(set.getInt("color"), set.getLong("role_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Set<Long> retrieveVIPs() {
        Set<Long> vips = new HashSet<>();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT linked_accounts.discord_id FROM linked_accounts, hypercube.ranks " +
                        "WHERE linked_accounts.player_uuid = ranks.uuid AND ranks.vip = 1"))
                .compile()
                .run(result -> {
                    for (ResultSet set : result) {
                        vips.add(set.getLong("discord_id"));
                    }
                });
        return vips;
    }

    /**
     * Whether a role is one of the bot's star roles: named "VIP", with an icon and no permissions.
     */
    public static boolean isStarRole(Role role) {
        return role.getName().equals(ROLE_NAME)
                && role.getIcon() != null
                && role.getPermissionsRaw() == 0
                && !role.isManaged();
    }

    /**
     * A member's name color from their other roles, so a star role never decides it.
     */
    public static int getNameColor(Member member) {
        for (Role role : member.getRoles()) {
            if (!isStarRole(role) && role.getColorRaw() != Role.DEFAULT_COLOR_RAW) {
                return role.getColorRaw();
            }
        }
        return Role.DEFAULT_COLOR_RAW;
    }

    /**
     * Drops mappings to roles that no longer exist, and adopts older star roles that carry their color,
     * so they are reused instead of created again.
     */
    public static void syncWithGuild(Guild guild) {
        COLOR_ROLE_MAP.entrySet().removeIf(entry -> {
            if (guild.getRoleById(entry.getValue()) != null) {
                return false;
            }
            forget(entry.getValue());
            return true;
        });

        for (Role role : guild.getRoles()) {
            int color = role.getColorRaw();
            if (isStarRole(role) && color != Role.DEFAULT_COLOR_RAW && !COLOR_ROLE_MAP.containsKey(color)) {
                remember(color, role.getIdLong());
            }
        }
    }

    /**
     * @return the existing star role for this color, or null
     */
    public static @Nullable Role getRole(Guild guild, int color) {
        Long roleId = COLOR_ROLE_MAP.get(color);
        return roleId == null ? null : guild.getRoleById(roleId);
    }

    /**
     * Creates the star role for a color. Blocks, so it must not be called from a JDA callback thread.
     *
     * @return the new role, or null if it could not be created
     */
    public static @Nullable Role createRole(Guild guild, int color) {
        try {
            ByteArrayOutputStream image = new ByteArrayOutputStream();
            ImageIO.write(StarUtil.create(new Color(color)), "png", image);
            Role role = guild.createRole()
                    .setName(ROLE_NAME)
                    .setIcon(Icon.from(image.toByteArray()))
                    .setPermissions(0L)
                    .reason("A VIP has this name color")
                    .complete();
            remember(color, role.getIdLong());
            return role;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes a star role and forgets it. Blocks until Discord confirms.
     */
    public static void deleteRole(Role role) {
        COLOR_ROLE_MAP.values().removeIf(id -> id == role.getIdLong());
        forget(role.getIdLong());

        try {
            role.delete()
                    .reason("No VIP uses this star role")
                    .complete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void remember(int color, long roleId) {
        COLOR_ROLE_MAP.put(color, roleId);
        new DatabaseQuery()
                .query(new BasicQuery("REPLACE INTO owen.vip_roles (color, role_id) VALUES (?, ?)", (statement) -> {
                    statement.setInt(1, color);
                    statement.setLong(2, roleId);
                }))
                .compile();
    }

    private static void forget(long roleId) {
        new DatabaseQuery()
                .query(new BasicQuery("DELETE FROM owen.vip_roles WHERE role_id = ?", (statement) -> statement.setLong(1, roleId)))
                .compile();
    }
}
