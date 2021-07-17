package com.diamondfire.helpbot.sys.tag;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class TagHandler {
    
    private static List<Tag> tags = new ArrayList<>();
    private static final File FILE = ExternalFiles.TAGS;
    
    public static List<Tag> getTags() throws IOException {
        return tags;
    }
    
    public static void cacheJson() throws IOException {
        // read the file and update the cache
        String content = new String(Files.readAllBytes(FILE.toPath()));
        if (content.length() == 0) content = "{}";
        JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
        
        for (String key : obj.keySet()) {
            JsonObject tag = obj.get(key).getAsJsonObject();
            tags.add(new Tag(tag.get("activator").getAsString(),
                    tag.get("title").getAsString(),
                    tag.get("response").getAsString(),
                    tag.get("authorId").getAsLong(),
                    tag.get("image").getAsString()));
        }
    }
    
    public static void saveToJson() throws IOException {
        JsonObject json = new JsonObject();
        
        for (Tag tag : tags) {
            JsonObject obj = new JsonObject();
            obj.addProperty("activator", tag.getActivator());
            obj.addProperty("title", tag.getTitle());
            obj.addProperty("response", tag.getResponse());
            obj.addProperty("authorId", tag.getAuthorId());
            obj.addProperty("image", tag.getImage());
            
            json.add(tag.getActivator(), obj);
        }
        
        FILE.delete();
        FILE.createNewFile();
        Files.write(FILE.toPath(), json.toString().getBytes(), StandardOpenOption.WRITE);
    }
    
    public static void newTag(Tag tag) throws TagAlreadyExistsException, IOException {
        // check if tag with equal activator already exists
        if (tags.stream().anyMatch(t -> t.getActivator().equals(tag.getActivator()))) {
            throw new TagAlreadyExistsException("A tag with this activator already exists.");
        }
        
        // implement the new tag
        tags.add(tag);
        saveToJson();
    }
    
    public static void deleteTag(String activator) throws TagDoesntExistException, IOException {
        // get tag + check if it exists
        Tag tag = getTag(activator);
        
        // remove the tag
        tags.remove(tag);
        saveToJson();
    }
    
    public static void deleteTag(Tag tag) throws TagDoesntExistException, IOException {
        deleteTag(tag.getActivator());
    }
    
    public static Tag getTag(String activator) throws TagDoesntExistException, IOException {
        List<Tag> tag = tags.stream().filter(t -> t.getActivator().equals(activator)).collect(Collectors.toList());
        if (tag.size() == 0) throw new TagDoesntExistException("A tag with activator `"+activator+"` does not exist.");
        return tag.get(0);
    }
}
