package com.diamondfire.helpbot.util;

public class FakeEmote {

    private final String name;
    private final long id;

    public FakeEmote(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public String getEmote() {
        return "<:" + name + ':' + id + '>';
    }
}
