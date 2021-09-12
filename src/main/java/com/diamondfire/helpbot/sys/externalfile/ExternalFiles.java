package com.diamondfire.helpbot.sys.externalfile;

import java.io.File;

public interface ExternalFiles {
    
    ExternalFile DB = new ExternalFileBuilder()
            .setName("db")
            .setFileType(ExternalFileType.JSON)
            .buildFile();
    
    ExternalFile DB_COMPARE = new ExternalFileBuilder()
            .setName("db_last")
            .setFileType(ExternalFileType.JSON)
            .buildFile();
    
    ExternalFile OTHER_CACHE_DIR = new ExternalFileBuilder()
            .isDirectory()
            .setName("other_cache")
            .buildFile();
    
    ExternalFile IMAGES_DIR = new ExternalFileBuilder()
            .isDirectory()
            .setName("images")
            .buildFile();
    
    ExternalFile SAM_DIR = new ExternalFileBuilder()
            .isDirectory()
            .setName("samquotes")
            .buildFile();
    
    ExternalFile CONFIG = new ExternalFileBuilder()
            .setName("config")
            .setFileType(ExternalFileType.JSON)
            .setDefaultContent("""
                    {
                      "token": "bot token",
                      "prefix": "?",
                      "dev_bot": true,

                      "mc_email": "Minecraft@email.com",
                      "mc_password": "MinecraftPassword",

                      "db_link": "jdbc:db_type://ip:port/schema",
                      "db_user": "dbuser",
                      "db_password": "dbpassword",

                      "guild": 0,
                      "log_channel": 0,
                      "discussion_channel": 0,
                      "muted_role": 0,
                      "verified_role": 0,

                      "role_react_channel": 0,
                      "role_react_message": 0

                    }""")
            .buildFile();
    
    ExternalFile DISABLED_COMMANDS = new ExternalFileBuilder()
            .setName("disabled_commands")
            .setFileType(ExternalFileType.TEXT)
            .buildFile();
    
    ExternalFile FILTER = new ExternalFileBuilder()
            .setName("swear_filter")
            .setFileType(ExternalFileType.JSON)
            .buildFile();
    
    ExternalFile SAMMAN = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("samman")
            .setFileType(ExternalFileType.PNG)
            .buildFile();
    
    ExternalFile TAGS = new ExternalFileBuilder()
            .setName("tags")
            .setFileType(ExternalFileType.JSON)
            .buildFile();
    
    ExternalFile SAM_QUOTES = new ExternalFileBuilder()
            .setName("samquotes")
            .setFileType(ExternalFileType.TEXT)
            .buildFile();
}
