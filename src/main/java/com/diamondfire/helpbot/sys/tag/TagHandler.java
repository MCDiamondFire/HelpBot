package com.diamondfire.helpbot.sys.tag;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.diamondfire.helpbot.sys.tag.exceptions.*;
import com.diamondfire.helpbot.util.serializer.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class TagHandler {
    private static Map<String, Tag> TAGS = new HashMap<>();
    
    static {
        try {
            cacheJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Map<String, Tag> getTags() {
        return TAGS;
    }
    
    public static void cacheJson() throws IOException {
        // Read the file and update the cache
        String content = Files.readString(ExternalFiles.TAGS);
        
        if (content.isEmpty()) {
            // Just short circuit here because there can't be any tags
            TAGS = new HashMap<>();
            return;
        }
        
        TAGS = HelpBotInstance.GSON.fromJson(content, new TypeToken<Map<String, Tag>>() {}.getType());
    }
    
    public static void save() throws IOException {
        Files.writeString(ExternalFiles.TAGS, HelpBotInstance.GSON.toJson(TAGS));
    }
    
    public static void newTag(Tag tag) throws TagAlreadyExistsException, IOException {
        // Check if tag with equal activator already exists
        if (TAGS.containsKey(tag.getActivator())) {
            throw new TagAlreadyExistsException("A tag with this activator already exists.");
        }
        
        // Add the new tag
        TAGS.put(tag.getActivator(), tag);
        save();
    }
    
    public static void deleteTag(String activator) throws IOException {
        TAGS.remove(activator);
        save();
    }
    
    public static void deleteTag(Tag tag) throws IOException {
        deleteTag(tag.getActivator());
    }
    
    public static void recache(String oldActivator, String activator) {
        TAGS.put(activator, TAGS.remove(oldActivator));
    }
    
    public static @NotNull Tag getTag(String activator) throws TagDoesNotExistException, IOException {
        Tag tag = TAGS.get(activator);
        
        if (tag == null) {
            throw new TagDoesNotExistException(String.format(
                    "A tag with activator `%s` does not exist.", activator));
        }
        
        return tag;
    }
    
}
