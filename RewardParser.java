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
    private String titleText;
    private String titleComponent;
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

        titleComponent = config.getString("title-component", "标题_字");
        hudYaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "questhud.yml"));

        String defaultTitleText = "§7当前共 §a [%xychemdahshow_task_amount%] 项委托待完成";
        String hudTitleText = hudYaml.getString(titleComponent + ".texts", "");
        if (config.contains("title-text")) {
            titleText = Texts.color(config.getString("title-text", defaultTitleText));
        } else if (containsInternalVariable(hudTitleText)) {
            titleText = Texts.color(hudTitleText);
        } else {
            titleText = Texts.color(defaultTitleText);
        }
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

    public String getTitleText() {
        return titleText;
    }

    public String getTitleComponent() {
        return titleComponent;
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

    private boolean containsInternalVariable(String text) {
        return text != null && text.contains("%xychemdahshow_");
    }
}