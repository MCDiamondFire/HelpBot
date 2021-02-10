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
            .setName("swear_filer")
            .setFileType("json")
            .buildFile();
}
