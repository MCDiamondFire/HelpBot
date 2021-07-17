package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.diamondfire.helpbot.sys.externalfile.ExternalFiles;
import com.google.gson.*;

import java.io.*;
import java.nio.file.*;

public class TagHandler {
    
    private static JsonObject json = null;
    private static final File FILE = ExternalFiles.TAGS;
    
    public static JsonObject getTags() throws IOException {
        // gets the cached json, and caches it if it is not yet cached
        if (json == null) cacheJson();
        return json;
    }
    
    private static void cacheJson() throws IOException {
        // read the file and update the cache
        String content = new String(Files.readAllBytes(FILE.toPath()));
        if (content.length() == 0) content = "{}";
        json = JsonParser.parseString(content).getAsJsonObject();
    }
    
    private static void updateJson(JsonObject newJson) throws IOException {
        // rewrite file
        FILE.delete();
        FILE.createNewFile();
        Files.write(FILE.toPath(), newJson.toString().getBytes(), StandardOpenOption.WRITE);
        
        // update cache
        json = newJson;
    }
    
    public static void newTag(Tag tag) throws TagAlreadyExistsException, IOException {
        // check if tag with equal activator already exists
        JsonObject tagJson = getTags().getAsJsonObject(tag.getActivator());
        if (tagJson != null) throw new TagAlreadyExistsException("A tag with this activator already exists.");
        
        // implement the new tag
        json.add(tag.getActivator(), tag.asJson());
        updateJson(json);
    }
    
    public static void deleteTag(String activator) throws TagDoesntExistException, IOException {
        // get tag + check if it exists
        Tag tag = getTag(activator);
        
        // remove the tag from the json
        json.remove(activator);
        updateJson(json);
    }
    
    public static void deleteTag(Tag tag) throws TagDoesntExistException, IOException {
        deleteTag(tag.getActivator());
    }
    
    public static Tag getTag(String activator) throws TagDoesntExistException, IOException {
        JsonObject tagJson = getTags().getAsJsonObject(activator);
        if (tagJson == null) throw new TagDoesntExistException("A tag with activator `"+activator+"` does not exist.");
        return new Tag(
                tagJson.get("activator").getAsString(),
                tagJson.get("title").getAsString(),
                tagJson.get("response").getAsString(),
                tagJson.get("authorId").getAsLong(),
                tagJson.get("image").getAsString()
        );
    }
    
    public static Tag getTag(Tag tag) throws TagDoesntExistException, IOException {
        return getTag(tag.getActivator());
    }
    
    public static void editTag(Tag oldTag, Tag newTag) throws TagDoesntExistException, TagAlreadyExistsException, IOException {
        deleteTag(oldTag);
        newTag(newTag);
    }
}
