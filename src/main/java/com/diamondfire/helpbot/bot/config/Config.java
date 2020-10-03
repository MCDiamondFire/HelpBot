package com.diamondfire.helpbot.bot.config;

import com.diamondfire.helpbot.sys.externalfile.ExternalFile;
import com.google.gson.*;

import java.io.*;
import java.util.stream.Collectors;

public class Config {

    private final JsonObject config;

    public Config() throws IllegalStateException {
        try (BufferedReader txtReader2 = new BufferedReader(new FileReader(ExternalFile.CONFIG.getFile().getPath()))) {
            String config = txtReader2.lines().collect(Collectors.joining());
            this.config = JsonParser.parseString(config).getAsJsonObject();
        } catch (Exception exception) {
            throw new IllegalStateException("Config not correctly structured! Please check the readme file for a config template.");
        }
    }

    // Bot Stuff
    public String getToken() {
        return getProperty("token");
    }

    public String getPrefix() {
        return getProperty("prefix");
    }

    public boolean isDevBot() {
        return config.get("dev_bot").getAsBoolean();
    }

    // Mc account
    public String getMcEmail() {
        return getProperty("mc_email");
    }

    public String getMcPassword() {
        return getProperty("mc_password");
    }

    // Database
    public String getDBUrl() {
        return getProperty("db_link");
    }

    public String getDBUser() {
        return getProperty("db_user");
    }

    public String getDBPassword() {
        return getProperty("db_password");
    }


    private String getProperty(String property) {
        return config.get(property).getAsString();
    }

}
