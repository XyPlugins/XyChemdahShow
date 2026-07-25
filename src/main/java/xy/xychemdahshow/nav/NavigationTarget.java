package xy.xychemdahshow.nav;

public final class NavigationTarget {

    private final String questId;
    private final String questName;
    private final String world;
    private final double x;
    private final double y;
    private final double z;

    public NavigationTarget(String questId, String questName, String world, double x, double y, double z) {
        this.questId = questId;
        this.questName = questName;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getQuestId() {
        return questId;
    }

    public String getQuestName() {
        return questName;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
