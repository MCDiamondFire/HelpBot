package com.diamondfire.helpbot.sys.tag;

import com.diamondfire.helpbot.util.serializer.TagSerializer;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Tag implements Serializable {
    
    private String activator;
    private String title;
    private String response;
    private long authorId;
    private String image;
    
    public Tag(String activator, String title, String response, long authorId, String image) {
        this.activator = activator;
        this.title = title;
        this.response = response;
        this.authorId = authorId;
        this.image = image;
    }
    
    public String getActivator() {
        return activator;
    }
    
    public void setActivator(String activator) {
        this.activator = activator.replaceAll(" ", "");
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        response = response.replace("\\n", "\n");
        this.response = response;
    }
    
    public long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public void sendResponse(TextChannel channel, @NotNull User requester) {
        
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(getTitle())
                .setDescription(getResponse() + "\n\u200b")
                .setColor(0x969dba)
                .setFooter("Executed by " + requester.getAsTag(), requester.getAvatarUrl());
        
        if (!getImage().isEmpty()) {
            embed.setImage(getImage());
        }
                
        channel.sendMessageEmbeds(embed.build()).queue();
    }
    
    public JsonObject serialize() {
        return TagSerializer.getInstance().serialize(this);
    }
    
}
