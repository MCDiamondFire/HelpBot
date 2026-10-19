package com.diamondfire.helpbot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

/** Resolves plot icons from materials, legacy usernames, and Base64 texture properties. */
public final class PlotIconUtil {
    private PlotIconUtil() {}

    public static @Nullable Thumbnail createThumbnail(@Nullable String icon, String attachmentName) {
        Icon resolved = resolve(icon);
        if (resolved.headUrl() != null) {
            return Thumbnail.fromUrl(resolved.headUrl()).withDescription("Plot icon");
        }
        return resolved.file() == null ? null : Thumbnail.fromFile(FileUpload.fromData(resolved.file(), attachmentName))
                .withDescription("Plot icon");
    }

    public static void applyToEmbed(@Nullable String icon, EmbedBuilder embed, MessageCreateBuilder message) {
        Icon resolved = resolve(icon);
        if (resolved.headUrl() != null) {
            embed.setThumbnail(resolved.headUrl());
        } else if (resolved.file() != null) {
            embed.setThumbnail("attachment://plot-icon.png");
            message.addFiles(FileUpload.fromData(resolved.file(), "plot-icon.png"));
        }
    }

    public static void applyToAuthor(@Nullable String icon, String name, String attachmentName,
                                     EmbedBuilder embed, MessageCreateBuilder message) {
        Icon resolved = resolve(icon);
        String iconUrl = resolved.headUrl();
        if (iconUrl == null && resolved.file() != null) {
            iconUrl = "attachment://" + attachmentName;
            message.addFiles(FileUpload.fromData(resolved.file(), attachmentName));
        }
        embed.setAuthor(name, null, iconUrl);
    }

    static @Nullable String getHeadUrl(@Nullable String icon) {
        if (icon == null || icon.length() <= 1) {
            return null;
        }
        if (icon.startsWith("h")) {
            String username = icon.substring(1);
            return username.matches("[A-Za-z0-9_]{1,16}") ? Util.getPlayerHead(username) : null;
        }
        if (!icon.startsWith("t")) {
            return null;
        }
        try {
            // Texture-based icons contain the complete Base64 textures property.
            String json = new String(Base64.getDecoder().decode(icon.substring(1)), StandardCharsets.UTF_8);
            JsonObject textures = JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("textures");
            String url = textures.getAsJsonObject("SKIN").get("url").getAsString();
            URI texture = URI.create(url);
            if (!("http".equals(texture.getScheme()) || "https".equals(texture.getScheme())) ||
                    !"textures.minecraft.net".equals(texture.getHost())) {
                return null;
            }
            String path = texture.getPath();
            if (path == null || !path.matches("/texture/[a-fA-F0-9]{32,64}")) {
                return null;
            }
            // Render the head, rather than displaying the flat Minecraft skin texture.
            return Util.getPlayerHead(path.substring("/texture/".length()));
        } catch (JsonParseException | IllegalArgumentException | IllegalStateException | NullPointerException |
                 ClassCastException | UnsupportedOperationException e) {
            return null;
        }
    }

    private static Icon resolve(@Nullable String icon) {
        String headUrl = getHeadUrl(icon);
        if (headUrl != null) {
            return new Icon(headUrl, null);
        }
        String material = icon == null || icon.isBlank() || icon.startsWith("h") || icon.startsWith("t")
                ? "BARRIER" : icon.toUpperCase(Locale.ROOT);
        File texture = Util.fetchMinecraftTextureFile(material);
        return new Icon(null, texture.isFile() ? texture : null);
    }

    private record Icon(@Nullable String headUrl, @Nullable File file) {}
}
