package xy.xychemdahshow.reward;

public final class RewardDefinition {

    private final String raw;
    private final String type;
    private final String id;
    private final int amount;

    public RewardDefinition(String raw, String type, String id, int amount) {
        this.raw = raw;
        this.type = type;
        this.id = id;
        this.amount = amount;
    }

    public String getRaw() {
        return raw;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}
