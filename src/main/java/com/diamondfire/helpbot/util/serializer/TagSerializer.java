package com.diamondfire.helpbot.util.serializer;

import com.diamondfire.helpbot.sys.tag.Tag;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class TagSerializer implements Serializer<JsonObject, Tag> {
    
    private static final TagSerializer instance = new TagSerializer();
    
    public static TagSerializer getInstance() {
        return instance;
    }
    
    @Override
    public JsonObject serialize(@NotNull Tag tag) {
        JsonObject json = new JsonObject();
    
        json.addProperty("activator", tag.getActivator());
        json.addProperty("title", tag.getTitle());
        json.addProperty("response", tag.getResponse());
        json.addProperty("authorId", tag.getAuthorId());
        json.addProperty("image", tag.getImage());
    
        return json;
    }
    
    @Override
    public Tag deserialize(@NotNull JsonObject toDeserialize) {
        return new Tag(
                toDeserialize.get("activator").getAsString(),
                toDeserialize.get("title").getAsString(),
                toDeserialize.get("response").getAsString(),
                toDeserialize.get("authorId").getAsLong(),
                toDeserialize.get("image").getAsString()
        );
    }
}
