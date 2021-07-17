package com.diamondfire.helpbot.bot.command.impl.other.tag;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public class Tag implements Cloneable {
    
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
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getResponse() {
        return this.response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public long getAuthorId() {
        return this.authorId;
    }
    
    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }
    
    public String getImage() {
        return this.image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public void set(TagProperty property, String newValue) {
        if (property == TagProperty.ACTIVATOR) setActivator(newValue);
        else if (property == TagProperty.TITLE) setTitle(newValue);
        else if (property == TagProperty.RESPONSE) setResponse(newValue);
        else if (property == TagProperty.IMAGE) setImage(newValue);
    }
    
    public JsonObject asJson() {
        JsonObject json = new JsonObject();
        json.addProperty("activator", this.activator);
        json.addProperty("title", this.title);
        json.addProperty("response", this.response);
        json.addProperty("authorId", this.authorId);
        json.addProperty("image", this.image);
        
        return json;
    }
    
    public void sendResponse(TextChannel channel) {
        User user = channel.getJDA().retrieveUserById(getAuthorId()).complete();
        
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(getTitle())
                .setDescription(getResponse()+"\n\u200b")
                .setColor(0x969dba)
                .setFooter("Written by "+user.getAsTag(), user.getAvatarUrl());
        if (!getImage().equals("")) embed.setImage(getImage());
                
        channel.sendMessage(embed.build()).queue();
    }
    
    @Override
    public Tag clone() throws CloneNotSupportedException {
        return (Tag) super.clone();
    }
}
