package com.diamondfire.helpbot.sys.reactions.multiselector;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.Contract;

import java.util.*;

public class MultiSelectorBuilder {

    private final ArrayList<MultiSelectorPage> pages = new ArrayList<>(10);
    private long channel;
    private long userID;

    @Contract("_, -> this")
    public MultiSelectorBuilder setUser(long userID) {
        this.userID = userID;
        return this;
    }

    @Contract("_, -> this")
    public MultiSelectorBuilder setChannel(long channel) {
        this.channel = channel;
        return this;
    }

    @Contract("_,_, -> this")
    public MultiSelectorBuilder addPage(String name, EmbedBuilder EmbedBuilder) {
        pages.add(new MultiSelectorPage(name, EmbedBuilder));
        return this;
    }

    @Contract("_,_,_ -> this")
    public MultiSelectorBuilder addPage(String name, EmbedBuilder EmbedBuilder, String customEmote) {
        pages.add(new MultiSelectorPage(name, EmbedBuilder, customEmote, false));
        return this;
    }

    @Contract("_,_,_, -> this")
    public MultiSelectorBuilder addPage(String name, EmbedBuilder EmbedBuilder, boolean hidden) {
        pages.add(new MultiSelectorPage(name, EmbedBuilder, hidden));
        return this;
    }

    @Contract("_,_,_,_, -> this")
    public MultiSelectorBuilder addPage(String name, EmbedBuilder EmbedBuilder, String customEmote, boolean hidden) {
        pages.add(new MultiSelectorPage(name, EmbedBuilder, customEmote, hidden));
        return this;
    }

    @Contract("-> this")
    public MultiSelectorBuilder orderPages() {
        pages.sort(Comparator.comparing(MultiSelectorPage::getName));
        return this;
    }

    public MultiSelector build() {
        return new MultiSelector(channel, userID, pages);
    }


}
