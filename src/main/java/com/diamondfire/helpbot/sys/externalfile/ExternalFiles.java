package com.diamondfire.helpbot.sys.externalfile;

import java.nio.file.Path;

public interface ExternalFiles {
    
    Path CONFIG = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("config.json")
            .build();
    
    Path DISABLED_COMMANDS = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("disabled_commands.txt")
            .build();
    
    Path FILTER = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("swear_filter.json")
            .build();
    
    Path DB = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("db.json")
            .build();
    
    Path DB_COMPARE = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("db_last.json")
            .build();
    
    Path OTHER_CACHE_DIR = new ExternalFileBuilder()
            .isDirectory(true)
            .setName("other_cache")
            .build();
    
    Path IMAGES_DIR = new ExternalFileBuilder()
            .isDirectory(true)
            .setName("images")
            .build();
    
    Path SAM_DIR = new ExternalFileBuilder()
            .isDirectory(true)
            .setName("samquotes")
            .build();
    
    Path SAMMAN = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("samman.png")
            .build();
    
    Path TAGS = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("tags.json")
            .build();
    
    Path SAM_QUOTES = new ExternalFileBuilder()
            .isDirectory(false)
            .setName("samquotes.txt")
            .build();
}
