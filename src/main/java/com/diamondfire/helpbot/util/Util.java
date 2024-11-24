package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.*;

public class Util {
    
    public static final long VERIFIED = HelpBotInstance.getConfig().getVerifiedRole();
    
    public static Deque<String> getUnicodeNumbers() {
        Deque<String> nums = new ArrayDeque<>();
        nums.add("\u0031\uFE0F\u20E3");
        nums.add("\u0032\uFE0F\u20E3");
        nums.add("\u0033\uFE0F\u20E3");
        nums.add("\u0034\uFE0F\u20E3");
        nums.add("\u0035\uFE0F\u20E3");
        nums.add("\u0036\uFE0F\u20E3");
        nums.add("\u0037\uFE0F\u20E3");
        nums.add("\u0038\uFE0F\u20E3");
        nums.add("\u0039\uFE0F\u20E3");
        nums.add("\uD83D\uDD1F");
        return nums;
    }
    
    public static UUID toUuid(String str) {
        if (str.contains("-")) {
            return UUID.fromString(str);
        } else {
            return UUID.fromString(str.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        }
    }
    
    public static File fetchMinecraftTextureFile(String fileName) {
        File imagesDir = ExternalFiles.IMAGES_DIR;
        try {
            File file = new File(imagesDir, fileName + ".png");
            
            if (file.exists()) {
                return file;
            } else {
                return new File(imagesDir, "BARRIER.png");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(imagesDir, "BARRIER.png");
    }
    
    public static String getPlayerHead(String player) {
        return "https://external-content.duckduckgo.com/iu/?reload=" + System.currentTimeMillis() + "&u=" + "https://mc-heads.net/head/" + URLEncoder.encode(player, StandardCharsets.UTF_8) + "/180";
    }
    
    /**
     * Converts a jsonArray into a String[]
     */
    public static String[] fromJsonArray(JsonArray jsonArray) {
        if (jsonArray == null) {
            return new String[]{};
        }
        String[] string = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            string[i] = jsonArray.get(i).getAsString();
        }
        
        return string;
    }
    
    public static int clamp(int num, int min, int max) {
        return Math.max(min, Math.min(num, max));
    }
    
    public static JsonObject getPlayerProfile(String player) {
        try {
            URL profile = new URL("https://mc-heads.net/minecraft/profile/" + player);
            URLConnection connection = profile.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                stringBuilder.append(inputLine);
            
            JsonElement element = JsonParser.parseString(stringBuilder.toString());
            if (!element.isJsonObject()) {
                return null;
            }
            
            return element.getAsJsonObject();
        } catch (IOException ignored) {
        }
        
        return null;
    }
    
    public static void log(PresetBuilder builder) {
        log(builder.getEmbed());
    }
    
    public static void log(EmbedBuilder builder) {
        HelpBotInstance.getJda().getTextChannelById(HelpBotInstance.LOG_CHANNEL).sendMessageEmbeds(builder.build()).queue();
    }
    
    public static void updateGuild(HashMap<Long, String> accounts, Guild guild) {
        Role verifiedRoles = guild.getRoleById(VERIFIED);
        guild.loadMembers((member) -> {
            updateMember(member, accounts.get(member.getIdLong()), verifiedRoles);
        });
        guild.pruneMemberCache();
    }
    
    public static void updateGuild(Guild guild) {
        HashMap<Long, String> accounts = new HashMap<>();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT discord_id AS discord_id, player_name AS player FROM linked_accounts WHERE discord_id IS NOT NULL AND player_name IS NOT NULL;"))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        try {
                            if (!accounts.containsKey(set.getLong("discord_id"))) {
                                accounts.put(set.getLong("discord_id"), set.getString("player"));
                            }
                            // big funny bad table
                        } catch (Exception ignored) {
                        }
                    }
                });
        updateGuild(accounts, guild);
    }
    
    public static void updateMember(Member member, String verifyName, Role verifiedRole) {
        Guild guild = member.getGuild();
        boolean canUpdateNickName = guild.getSelfMember().hasPermission(Permission.NICKNAME_MANAGE);
        boolean canUpdateRole = guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES);
        
        // If user isn't verified
        if (guild.getSelfMember().canInteract(member)) {
            if (verifyName == null) {
                // Remove verified role if they have it
                if (member.getRoles().contains(verifiedRole) && verifiedRole != null && canUpdateRole) {
                    guild.removeRoleFromMember(member, verifiedRole)
                            .reason("Removing user's verified role, because they are not verified on DiamondFire.")
                            .queue();
                    
                    member.getUser()
                            .openPrivateChannel()
                            .flatMap((channel) -> channel.sendMessage("You have been unverified on DiamondFire because you no longer have a Minecraft account associated with your Discord account."))
                            .queue();
                }
                if (canUpdateNickName) {
                    guild.modifyNickname(member, null)
                            .reason("Removing user's nickname, because they are not verified on DiamondFire.")
                            .queue();
                }
                
            } else {
                // Remove verified role if they have it
                if (!member.getRoles().contains(verifiedRole) && verifiedRole != null && canUpdateRole) {
                    guild.addRoleToMember(member, verifiedRole)
                            .reason("Giving user verified role, because they are verified on DiamondFire.")
                            .queue();
                }
                
                if (!member.getEffectiveName().equals(verifyName) && canUpdateNickName) {
                    guild.modifyNickname(member, verifyName)
                            .reason("Updating user's nickname to reflect their name on DiamondFire.")
                            .queue();
                }
                
            }
        }
    }
    
    public static void updateMember(Member member) {
        new DatabaseQuery()
                .query(new BasicQuery("SELECT player_name AS name FROM linked_accounts WHERE discord_id = ?;", (statement) -> {
                    statement.setLong(1, member.getIdLong());
                }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        return;
                    }
                    
                    ResultSet table = result.getResult();
                    updateMember(member, table.getString("name"), member.getGuild().getRoleById(VERIFIED));
                });
    }
    
    public static List<ActionRow> of(List<Button> components) {
        Deque<Button> buttons = new ArrayDeque<>(components);
        List<Button> queue = new ArrayList<>();
        
        List<ActionRow> rows = new ArrayList<>();
        
        while (!buttons.isEmpty()) {
            queue.add(buttons.pop());
            if (queue.size() >= 5) {
                rows.add(ActionRow.of(queue));
                queue.clear();
            }
        }
        
        if (!queue.isEmpty()) {
            rows.add(ActionRow.of(queue));
        }
        
        return rows;
    }
}
