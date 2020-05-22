package com.diamondfire.helpbot.components;

import com.diamondfire.helpbot.util.BotConstants;

import java.io.File;
import java.io.IOException;

public class ExternalFileHandler {
    public static File DB = null;
    public static File DB_COMPARE = null;
    public static File MAIN_FILE = null;
    public static File HEAD_CACHE = null;
    public static File MISC_CACHE = null;
    public static File IMAGES = null;

    //TODO lol clean this hunk of junk up!!!!
    public static void initialize() {
        File mainFile = new File(BotConstants.DEBUG_MODE ? BotConstants.DEBUG_PATH : BotConstants.MAIN_PATH);

        boolean debug = BotConstants.DEBUG_MODE;
        if (!mainFile.exists()) {
            System.out.println("Main directory doesn't exist... creating!");
            mainFile.mkdir();
        }
        MAIN_FILE = mainFile;

        File headCache = new File((debug ? BotConstants.DEBUG_PATH + "\\" : "") + "head_cache");

        if (!headCache.exists()) {
            System.out.println("headCache directory doesn't exist... creating!");
            headCache.mkdir();
        }
        HEAD_CACHE = headCache;


        File miscCache = new File((debug ? BotConstants.DEBUG_PATH + "\\" : "") + "misc_cache");

        if (!miscCache.exists()) {
            System.out.println("miscCache directory doesn't exist... creating!");
            miscCache.mkdir();
        }
        MISC_CACHE = miscCache;

        File images = new File((debug ? BotConstants.DEBUG_PATH + "\\" : "") + "images");

        if (!images.exists()) {
            System.out.println("image directory doesn't exist... creating!");
            images.mkdir();
        }
        IMAGES = images;

        DB = new File((debug ? BotConstants.DEBUG_PATH + "\\" : "") + "db.json");

        DB_COMPARE = new File((debug ? BotConstants.DEBUG_PATH + "\\" : "") + "db_last.json");
    }

    public static File generateFile(String name) throws IOException {

        File file = new File(MISC_CACHE + "/" + name);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

}
