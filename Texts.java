package xy.xychemdahshow.config;

import xy.xychemdahshow.reward.RewardDefinition;

import java.util.Collections;
import java.util.List;

public final class QuestView {

    private final String questId;
    private final String name;
    private final int weight;
    private final List<String> text;
    private final List<RewardDefinition> rewards;

    public QuestView(String questId, String name, int weight, List<String> text, List<RewardDefinition> rewards) {
        this.questId = questId;
        this.name = name;
        this.weight = weight;
        this.text = Collections.unmodifiableList(text);
        this.rewards = Collections.unmodifiableList(rewards);
    }

    public String getQuestId() {
        return questId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public List<String> getText() {
        return text;
    }

    public List<RewardDefinition> getRewards() {
        return rewards;
    }
}
