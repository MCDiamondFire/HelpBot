package com.diamondfire.helpbot.sys.tag;

import com.diamondfire.helpbot.sys.externalfile.*;
import com.diamondfire.helpbot.sys.tag.exceptions.*;
import com.diamondfire.helpbot.util.serializer.*;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class TagHandler {
    
    private static final List<Tag> TAGS = new ArrayList<>();
    private static final ExternalFile FILE = ExternalFiles.TAGS;
    
    static {
        try {
            cacheJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Tag> getTags() {
        return TAGS;
    }
    
    public static void cacheJson() throws IOException {
        // Read the file and update the cache
        JsonObject json = FILE.parseJson();
        
        List<JsonObject> objects = json.keySet().stream()
                .map(key -> json.get(key).getAsJsonObject())
                .collect(Collectors.toList());
        
        for (JsonObject object : objects) {
            TAGS.add(TagSerializer.getInstance().deserialize(
                    object
            ));
        }
    }
    
    public static void save() throws IOException {
        JsonObject json = new JsonObject();
        
        TAGS.forEach(tag -> json.add(
                tag.getActivator(), tag.serialize()
        ));
    
        FILE.write(json);
    }
    
    public static void newTag(Tag tag) throws TagAlreadyExistsException, IOException {
        // Check if tag with equal activator already exists
        if (TAGS.stream().anyMatch(t -> t.getActivator().equals(tag.getActivator()))) {
            throw new TagAlreadyExistsException("A tag with this activator already exists.");
        }
        
        // Add the new tag
        TAGS.add(tag);
        save();
    }
    
    public static void deleteTag(String activator) throws TagDoesNotExistException, IOException {
        // Get tag + check if it exists
        Tag tag = getTag(activator);
        
        // Remove the tag
        TAGS.remove(tag);
        save();
    }
    
    public static void deleteTag(Tag tag) throws TagDoesNotExistException, IOException {
        deleteTag(tag.getActivator());
    }
    
    public static @NotNull Tag getTag(String activator) throws TagDoesNotExistException, IOException {
        Tag tag = TAGS.stream()
                .filter(t -> t.getActivator().equals(activator))
                .collect(Collectors.toList())
                .get(0);
        
        if (tag == null) {
            throw new TagDoesNotExistException(String.format(
                    "A tag with activator `%s` does not exist.", activator));
        }
        
        return tag;
    }
    
}
