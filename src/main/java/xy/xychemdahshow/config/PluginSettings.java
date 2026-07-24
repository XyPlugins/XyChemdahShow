package xy.xychemdahshow.config;

import org.bukkit.configuration.file.YamlConfiguration;
import xy.xychemdahshow.XyChemdahShow;
import xy.xychemdahshow.util.Texts;

import java.io.File;

public final class PluginSettings {

    private final XyChemdahShow plugin;
    private int hudDelay;
    private int progressRefreshDelay;
    private int joinDelay;
    private boolean deleteHud;
    private String emptyText;
    private boolean taskProgressEnabled;
    private String taskProgressFormat;
    private String taskCompletedProgressFormat;
    private YamlConfiguration hudYaml;

    public PluginSettings(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));

        hudDelay = Math.max(0, config.getInt("huddelay", 30));
        progressRefreshDelay = Math.max(0, config.getInt("progress-refresh-delay", 3));
        joinDelay = Math.max(0, config.getInt("joindelay", 60));
        deleteHud = config.getBoolean("deletehud", false);
        emptyText = Texts.color(config.getString("empty-text", "§7暂无正在进行的任务"));
        taskProgressEnabled = config.getBoolean("task-progress-enabled", true);
        taskProgressFormat = Texts.color(config.getString("task-progress-format", " §8[§a%current%§7/§e%target%§8]"));
        taskCompletedProgressFormat = Texts.color(config.getString("task-completed-progress-format", " §8[§a%current%§7/§a%target%§8]"));
        hudYaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "questhud.yml"));
    }

    public int getHudDelay() {
        return hudDelay;
    }

    public int getProgressRefreshDelay() {
        return progressRefreshDelay;
    }

    public int getJoinDelay() {
        return joinDelay;
    }

    public boolean isDeleteHud() {
        return deleteHud;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public boolean isTaskProgressEnabled() {
        return taskProgressEnabled;
    }

    public String getTaskProgressFormat() {
        return taskProgressFormat;
    }

    public String getTaskCompletedProgressFormat() {
        return taskCompletedProgressFormat;
    }

    public YamlConfiguration getHudYaml() {
        return hudYaml;
    }
}