package com.diamondfire.helpbot.bot.config;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.google.gson.*;

import java.io.*;
import java.util.stream.Collectors;

public class Config {
    
    private final JsonObject config;
    
    public Config() throws IllegalStateException {
        try (BufferedReader txtReader2 = new BufferedReader(new FileReader(ExternalFiles.CONFIG.getPath()))) {
            String config = txtReader2.lines().collect(Collectors.joining());
            this.config = JsonParser.parseString(config).getAsJsonObject();
        } catch (Exception exception) {
            throw new IllegalStateException("Config not correctly structured! Please check the readme file for a config template.");
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
    
    public long getGuild() {
        return getPropertyLong("guild");
    }
    
    public long getLogChannel() {
        return getPropertyLong("log_channel");
    }
    
    public long getDiscussionChannel() {
        return getPropertyLong("discussion_channel");
    }
    
    public long getMemesChannel() { return getPropertyLong("memes_channel"); }
    
    public long getBestMemesChannel() { return getPropertyLong("best_memes_channel"); }
    
    public long getMutedRole() {
        return getPropertyLong("muted_role");
    }
    
    public long getVerifiedRole() {
        return getPropertyLong("verified_role");
    }
    
    private long getPropertyLong(String property) {
        return config.get(property).getAsLong();
    }
    
    private String getPropertyString(String property) {
        return config.get(property).getAsString();
    }
    
}
