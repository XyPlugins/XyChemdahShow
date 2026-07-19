package com.xy.xychemdahshow.pojo;

import java.util.List;

public class UI {
    private final String questID;
    private final String name;
    private final int weight;
    private final List<String> text;
    private final List<String> reward;

    public UI(String questID, String name, int weight, List<String> text, List<String> reward) {
        this.questID = questID;
        this.name = name;
        this.weight = weight;
        this.text = text;
        this.reward = reward;
    }

    public String getQuestID() { return questID; }
    public String getName() { return name; }
    public int getWeight() { return weight; }
    public List<String> getText() { return text; }
    public List<String> getReward() { return reward; }
}