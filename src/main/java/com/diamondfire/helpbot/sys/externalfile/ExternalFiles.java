package com.diamondfire.helpbot.sys.externalfile;

import java.io.File;
import java.nio.file.Path;

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
    
    Path TAGS = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("tags")
            .setFileType("json")
            .build();
    
    Path SAM_QUOTES = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("samquotes")
            .setFileType("txt")
            .build();
}
