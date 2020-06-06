package com.diamondfire.helpbot.components.reactions.multiselector;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MultiSelectorBuilder {
    private ArrayList<MultiSelectorPage> pages = new ArrayList<>(10);
    private long channel;
    private long userID;

    public MultiSelectorBuilder setUser(long userID) {
        this.userID = userID;
        return this;
    }

    public MultiSelectorBuilder setChannel(long channel) {
        this.channel = channel;
        return this;
    }

    public MultiSelectorBuilder addPage(String name, EmbedBuilder EmbedBuilder) {
        pages.add(new MultiSelectorPage(name, EmbedBuilder));
        return this;
    }

    public MultiSelector build() {
        return new MultiSelector(channel, userID, pages);
    }


}
