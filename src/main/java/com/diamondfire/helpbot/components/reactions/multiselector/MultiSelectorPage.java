package com.diamondfire.helpbot.components.reactions.multiselector;

import net.dv8tion.jda.api.EmbedBuilder;

public class MultiSelectorPage {
        private final String name;
        private final EmbedBuilder page;

        public MultiSelectorPage(String name, EmbedBuilder page) {
            this.name = name;
            this.page = page;
            page.setTitle(name);
        }

        public String getName() {
            return name;
        }

        public EmbedBuilder getPage() {
            return page;
        }

}
