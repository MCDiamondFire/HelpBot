package com.diamondfire.helpbot.sys.externalfile;

import java.io.File;

public interface ExternalFiles {
    
    File DB = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("db")
            .setFileType("json")
            .buildFile();
    
    File DB_COMPARE = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("db_last")
            .setFileType("json")
            .buildFile();
    
    File OTHER_CACHE_DIR = new ExternalFileBuilder()
            .isDirectory(true)
            .setName("other_cache")
            .buildFile();
    
    File IMAGES_DIR = new ExternalFileBuilder()
            .isDirectory(true)
            .setName("images")
            .buildFile();
    
    File SAM_DIR = new ExternalFileBuilder()
            .isDirectory(true)
            .setName("samquotes")
            .buildFile();
    
    File CONFIG = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("config")
            .setFileType("json")
            .buildFile();
    
    File DISABLED_COMMANDS = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("disabled_commands")
            .setFileType("txt")
            .buildFile();
    
    File FILTER = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("swear_filter")
            .setFileType("json")
            .buildFile();
    
    File SAMMAN = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("samman")
            .setFileType("png")
            .buildFile();
    
    File TAGS = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("tags")
            .setFileType("json")
            .buildFile();
    
    File VIP_ROLES = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("vip_roles")
            .setFileType("json")
            .buildFile();
    
    File SAM_QUOTES = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("samquotes")
            .setFileType("txt")
            .buildFile();
}
