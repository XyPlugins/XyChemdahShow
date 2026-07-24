package xy.xychemdahshow.reward;

import java.util.ArrayList;
import java.util.List;

public final class RewardParser {

    private RewardParser() {
    }

    public static List<RewardDefinition> parse(List<String> rawRewards) {
        List<RewardDefinition> result = new ArrayList<RewardDefinition>();
        for (String raw : rawRewards) {
            RewardDefinition reward = parseOne(raw);
            if (reward != null) {
                result.add(reward);
            }
        }
        return result;
    }

    private static RewardDefinition parseOne(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        String[] split = raw.split(":", 3);
        if (split.length < 2) {
            return new RewardDefinition(raw, "raw", raw.trim(), 1);
        }

        String type = split[0].trim();
        String id = split[1].trim();
        int amount = 1;
        if (split.length == 3) {
            try {
                amount = Math.max(1, Integer.parseInt(split[2].trim()));
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        }
        return new RewardDefinition(raw, type, id, amount);
    }
}
