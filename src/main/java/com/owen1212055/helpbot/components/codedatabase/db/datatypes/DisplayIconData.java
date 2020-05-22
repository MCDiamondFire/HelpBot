package com.owen1212055.helpbot.components.codedatabase.db.datatypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.owen1212055.helpbot.util.CompressionUtil;
import com.owen1212055.helpbot.components.ExternalFileHandler;
import com.owen1212055.helpbot.util.Util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DisplayIconData {

    private final JsonObject icon;

    /**
     * An item icon, which is an item that is represented in menus.
     *
     * @param icon The icon raw json data.
     */
    public DisplayIconData(JsonObject icon) {
        this.icon = icon;
    }

    /**
     * @return The name of the item.
     */
    public String getItemName() {
        return this.icon.get("name").getAsString();
    }

    /**
     * @return The material of the item.
     */
    public String getMaterial() {
        return this.icon.get("material").getAsString();
    }

    /**
     * @return The description which basically describes it.
     */
    public String[] getDescription() {
        return Util.jsonArrayToString(this.icon.get("description").getAsJsonArray());
    }

    /**
     * @return Examples of how this is used.
     */
    public String[] getExample() {
        return Util.jsonArrayToString(this.icon.get("example").getAsJsonArray());
    }

    /**
     * @return Any info in the "workswith" section.
     */
    public String[] getWorksWith() {
        return Util.jsonArrayToString(this.icon.get("worksWith").getAsJsonArray());
    }

    /**
     * @return Any information shown in the lore "blah"
     */
    public JsonArray getAdditionalInfo() {
        return this.icon.get("additionalInfo").getAsJsonArray();
    }

    /**
     * @return If this required a rank to use.
     */
    public String getRequiredRank() {
        return this.icon.get("requiredRank").getAsString();
    }

    /**
     * @return If this requires credits to use.
     */
    public boolean getRequiredCredits() {
        return this.icon.get("requireCredits").getAsBoolean();
    }

    /**
     * @return If this requires a rank AND credits.
     */
    public boolean getRequiredRanksOrCredits() {
        return this.icon.get("requireRankAndCredits").getAsBoolean();
    }

    /**
     * @return If this action is cancelled automatically. (Event Actions only)
     */
    public CodeBlockActionArgumentData[] getParameters() {

        return this.icon.get("arguments") == null ? new CodeBlockActionArgumentData[]{} : CodeBlockActionArgumentData.parse(this.icon.get("arguments").getAsJsonArray()).toArray(new CodeBlockActionArgumentData[]{});
    }

    /**
     * @return If this action only effects mobs. (Mobs Actions only)
     */
    public boolean mobsOnly() {
        return this.icon.get("mobsOnly") != null && this.icon.get("mobsOnly").getAsBoolean();
    }

    /**
     * @return If this action is able to be cancelled. (Event Actions only)
     */
    public boolean isCancellable() {
        return this.icon.get("cancellable") != null && this.icon.get("cancellable").getAsBoolean();
    }

    /**
     * @return If this action is cancelled automatically. (Event Actions only)
     */
    public boolean isCancelledAutomatically() {
        return this.icon.get("cancelledAutomatically") != null && this.icon.get("cancelledAutomatically").getAsBoolean();
    }

    /**
     * @return The return type, which is the type of variable that it returns. (Gamevalues only)
     */
    public String getReturnType() {
        return this.icon.get("returnType").getAsString();
    }

    /**
     * @return The return description, which gives detail on the type of variable that it returns. (Gamevalues only)
     */
    public String[] getReturnDescription() {
        return Util.jsonArrayToString(this.icon.get("returnDescription").getAsJsonArray());
    }

    public File getHead() {
        if (this.icon.get("head") == null) {
            return null;
        }
        JsonObject format = JsonParser.parseString(new String(CompressionUtil.fromBase64(this.icon.get("head").getAsString().getBytes()))).getAsJsonObject();


        String texture = format.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString().substring(38);

        File check = new File(ExternalFileHandler.HEAD_CACHE.getPath() + "/"  + texture + ".png");
        if (check.exists()) {
            return check;
        }

        String url = String.format("https://mc-heads.net/head/%s", texture);
        try {
            URL website = new URL(url);
            InputStream inputStream = website.openStream();
            Files.copy(inputStream, Paths.get(ExternalFileHandler.HEAD_CACHE.getPath() + "/" + texture + ".png"), StandardCopyOption.REPLACE_EXISTING);
            return new File(ExternalFileHandler.HEAD_CACHE.getPath() + "/" + texture + ".png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


}
