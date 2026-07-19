package com.xy.xychemdahshow.pojo;

import java.util.List;

public class Hud {
    private final String questID;
    private final String name;
    private final int weight;
    private final List<String> text;

    public Hud(String questID, String name, int weight, List<String> text) {
        this.questID = questID;
        this.name = name;
        this.weight = weight;
        this.text = text;
    }

    public String getQuestID() { return questID; }
    public String getName() { return name; }
    public int getWeight() { return weight; }
    public List<String> getText() { return text; }
}