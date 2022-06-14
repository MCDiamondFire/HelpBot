package com.diamondfire.helpbot.bot.config;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

public class Config {
    private final JsonObject config;
    
    public Config() throws IllegalStateException {
        try {
            this.config = HelpBotInstance.GSON.fromJson(Files.readString(ExternalFiles.CONFIG.toPath()), JsonObject.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Config is correctly structured! Please check the readme file for a config template.");
        }
    }
    
    // Bot Stuff
    public String getToken() {
        return getPropertyString("token");
    }
    
    public String getPrefix() {
        return getPropertyString("prefix");
    }
    
    public boolean isDevBot() {
        return config.get("dev_bot").getAsBoolean();
    }
    
    // Mc account
    public String getMcEmail() {
        return getPropertyString("mc_email");
    }
    
    public String getMcPassword() {
        return getPropertyString("mc_password");
    }
    
    // Database
    public String getDBUrl() {
        return getPropertyString("db_link");
    }
    
    public String getDBUser() {
        return getPropertyString("db_user");
    }
    
    public String getDBPassword() {
        return getPropertyString("db_password");
    }
    
    // General constants
    
    public String getlabsCoreToken() {
        return getPropertyString("labs_token");
    }
    
    public long getGuild() {
        return getPropertyLong("guild");
    }
    
    public String getReportWehook() {
        return getPropertyString("report_webhook");
    }
    
    public Map<String, Long> getPermissionRoleMap() {
        return HelpBotInstance.GSON.fromJson(config.get("permission_roles").getAsJsonObject(), new TypeToken<Map<String, Long>>(){}.getType());
    }
    
    // Channels
    
    public long getLogChannel() {
        return getPropertyLong("log_channel");
    }
    
    public long getDiscussionChannel() {
        return getPropertyLong("discussion_channel");
    }
    
    public long getPurgeEvidenceChannel() {
        return getPropertyLong("purge_evidence_channel");
    }
    
    public long getGraphChannel() {
        return getPropertyLong("graph_channel");
    }
    
    public long getExpertChatChannel() {
        return getPropertyLong("expert_chat_channel");
    }
    
    // Roles
    
    public long getMutedRole() {
        return getPropertyLong("muted_role");
    }
    
    public long getVerifiedRole() {
        return getPropertyLong("verified_role");
    }
    
    // Getters
    
    private long getPropertyLong(String property) {
        return config.get(property).getAsLong();
    }
    
    private String getPropertyString(String property) {
        return config.get(property).getAsString();
    }
    
}
