package xy.xychemdahshow.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xy.xychemdahshow.XyChemdahShow;
import xy.xychemdahshow.util.Texts;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PluginSettings {

    private static final String HUD_NAVIGATION_PATH = "任务导航按钮.navigation.";

    private final XyChemdahShow plugin;
    private int hudDelay;
    private boolean hudKeepAliveEnabled;
    private int hudKeepAliveInterval;
    private boolean hudKeepAliveReopen;
    private int progressRefreshDelay;
    private int joinDelay;
    private boolean deleteHud;
    private String emptyText;
    private boolean taskProgressEnabled;
    private String taskProgressFormat;
    private String taskCompletedProgressFormat;
    private String structuredLineFormat;
    private String structuredTypeLabel;
    private String structuredLocationLabel;
    private String structuredTargetLabel;
    private String structuredDetailLabel;
    private boolean navigationEnabled;
    private int navigationParticleInterval;
    private double navigationArriveDistance;
    private double navigationParticleSpacing;
    private double navigationArrowHeadLength;
    private int navigationMaxPoints;
    private boolean navigationGroundFollowEnabled;
    private int navigationGroundSearchUp;
    private int navigationGroundSearchDown;
    private double navigationGroundOffset;
    private String navigationParticle;
    private String navigationRenderMode;
    private String navigationDragonCoreArrowTexture;
    private double navigationDragonCoreArrowWidth;
    private double navigationDragonCoreArrowHeight;
    private double navigationDragonCoreArrowSpacing;
    private int navigationDragonCoreArrowMaxPoints;
    private int navigationDragonCoreArrowUpdateInterval;
    private double navigationDragonCoreArrowRotationX;
    private double navigationDragonCoreArrowRotationYOffset;
    private double navigationDragonCoreArrowRotationZ;
    private double navigationDragonCoreArrowAlpha;
    private boolean navigationDragonCoreArrowThrough;
    private boolean navigationDragonCoreArrowGlow;
    private YamlConfiguration hudYaml;
    private Map<String, String> hudVariableTextTemplates = Collections.emptyMap();

    public PluginSettings(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        hudYaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "questhud.yml"));

        hudDelay = Math.max(0, config.getInt("huddelay", 30));
        hudKeepAliveEnabled = config.getBoolean("hud-keep-alive.enabled", true);
        hudKeepAliveInterval = Math.max(20, config.getInt("hud-keep-alive.interval", 100));
        hudKeepAliveReopen = config.getBoolean("hud-keep-alive.reopen-hud", false);
        progressRefreshDelay = Math.max(0, config.getInt("progress-refresh-delay", 3));
        joinDelay = Math.max(0, config.getInt("joindelay", 60));
        deleteHud = config.getBoolean("deletehud", false);
        emptyText = Texts.color(config.getString("empty-text", "§7暂无正在进行的任务"));
        taskProgressEnabled = config.getBoolean("task-progress-enabled", true);
        taskProgressFormat = Texts.color(config.getString("task-progress-format", " §8[§a%current%§7/§e%target%§8]"));
        taskCompletedProgressFormat = Texts.color(config.getString("task-completed-progress-format", " §8[§a%current%§7/§a%target%§8]"));
        structuredLineFormat = Texts.color(config.getString("structured-line-format", "&7%label%: &f%value%"));
        structuredTypeLabel = Texts.color(config.getString("structured-labels.type", "类型"));
        structuredLocationLabel = Texts.color(config.getString("structured-labels.location", "地点"));
        structuredTargetLabel = Texts.color(config.getString("structured-labels.target", "目标"));
        structuredDetailLabel = Texts.color(config.getString("structured-labels.detail", "详情"));
        navigationEnabled = config.getBoolean("navigation-enabled", config.getBoolean("particle-navigation-enabled", config.getBoolean("navigation.enabled", true)));
        navigationParticleInterval = Math.max(1, config.getInt("navigation.particle-interval", 8));
        navigationArriveDistance = Math.max(0.5D, config.getDouble("navigation.arrive-distance", 3.0D));
        navigationParticleSpacing = Math.max(0.4D, config.getDouble("navigation.particle-spacing", 0.9D));
        navigationArrowHeadLength = Math.max(0.6D, config.getDouble("navigation.arrow-head-length", 1.4D));
        navigationMaxPoints = Math.max(8, config.getInt("navigation.max-points", 80));
        navigationGroundFollowEnabled = config.getBoolean("navigation.ground-follow-enabled", true);
        navigationGroundSearchUp = Math.max(0, config.getInt("navigation.ground-search-up", 4));
        navigationGroundSearchDown = Math.max(1, config.getInt("navigation.ground-search-down", 48));
        navigationGroundOffset = Math.max(0.05D, config.getDouble("navigation.ground-offset", 0.15D));
        navigationParticle = config.getString("navigation.particle", "VILLAGER_HAPPY");
        navigationRenderMode = getNavigationString(config, "render-mode", "navigation.render-mode", "particle");
        navigationDragonCoreArrowTexture = getNavigationString(config, "dragoncore-arrow.texture", "navigation.dragoncore-arrow.texture", "xychemdahshow/nav_arrow.png");
        navigationDragonCoreArrowWidth = Math.max(0.05D, getNavigationDouble(config, "dragoncore-arrow.width", "navigation.dragoncore-arrow.width", 0.65D));
        navigationDragonCoreArrowHeight = Math.max(0.05D, getNavigationDouble(config, "dragoncore-arrow.height", "navigation.dragoncore-arrow.height", 0.65D));
        navigationDragonCoreArrowSpacing = Math.max(0.3D, getNavigationDouble(config, "dragoncore-arrow.spacing", "navigation.dragoncore-arrow.spacing", 1.4D));
        navigationDragonCoreArrowMaxPoints = Math.max(1, getNavigationInt(config, "dragoncore-arrow.max-points", "navigation.dragoncore-arrow.max-points", 48));
        navigationDragonCoreArrowUpdateInterval = Math.max(1, getNavigationInt(config, "dragoncore-arrow.update-interval", "navigation.dragoncore-arrow.update-interval", 2));
        navigationDragonCoreArrowRotationX = getNavigationDouble(config, "dragoncore-arrow.rotation-x", "navigation.dragoncore-arrow.rotation-x", 90D);
        navigationDragonCoreArrowRotationYOffset = getNavigationDouble(config, "dragoncore-arrow.rotation-y-offset", "navigation.dragoncore-arrow.rotation-y-offset", 90D);
        navigationDragonCoreArrowRotationZ = getNavigationDouble(config, "dragoncore-arrow.rotation-z", "navigation.dragoncore-arrow.rotation-z", 0D);
        navigationDragonCoreArrowAlpha = Math.max(0D, Math.min(1D, getNavigationDouble(config, "dragoncore-arrow.alpha", "navigation.dragoncore-arrow.alpha", 1.0D)));
        navigationDragonCoreArrowThrough = getNavigationBoolean(config, "dragoncore-arrow.through", "navigation.dragoncore-arrow.through", false);
        navigationDragonCoreArrowGlow = getNavigationBoolean(config, "dragoncore-arrow.glow", "navigation.dragoncore-arrow.glow", true);

        stripPluginOnlyHudSettings();
        hudVariableTextTemplates = scanHudVariableTextTemplates(hudYaml);
    }

    public int getHudDelay() {
        return hudDelay;
    }

    public boolean isHudKeepAliveEnabled() {
        return hudKeepAliveEnabled;
    }

    public int getHudKeepAliveInterval() {
        return hudKeepAliveInterval;
    }

    public boolean isHudKeepAliveReopen() {
        return hudKeepAliveReopen;
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

    public String getStructuredLineFormat() {
        return structuredLineFormat;
    }

    public String getStructuredTypeLabel() {
        return structuredTypeLabel;
    }

    public String getStructuredLocationLabel() {
        return structuredLocationLabel;
    }

    public String getStructuredTargetLabel() {
        return structuredTargetLabel;
    }

    public String getStructuredDetailLabel() {
        return structuredDetailLabel;
    }

    public boolean isNavigationEnabled() {
        return navigationEnabled;
    }

    public int getNavigationParticleInterval() {
        return navigationParticleInterval;
    }

    public double getNavigationArriveDistance() {
        return navigationArriveDistance;
    }

    public double getNavigationParticleSpacing() {
        return navigationParticleSpacing;
    }

    public double getNavigationArrowHeadLength() {
        return navigationArrowHeadLength;
    }

    public int getNavigationMaxPoints() {
        return navigationMaxPoints;
    }

    public boolean isNavigationGroundFollowEnabled() {
        return navigationGroundFollowEnabled;
    }

    public int getNavigationGroundSearchUp() {
        return navigationGroundSearchUp;
    }

    public int getNavigationGroundSearchDown() {
        return navigationGroundSearchDown;
    }

    public double getNavigationGroundOffset() {
        return navigationGroundOffset;
    }

    public String getNavigationParticle() {
        return navigationParticle;
    }

    public String getNavigationRenderMode() {
        return navigationRenderMode;
    }

    public String getNavigationDragonCoreArrowTexture() {
        return navigationDragonCoreArrowTexture;
    }

    public double getNavigationDragonCoreArrowWidth() {
        return navigationDragonCoreArrowWidth;
    }

    public double getNavigationDragonCoreArrowHeight() {
        return navigationDragonCoreArrowHeight;
    }

    public double getNavigationDragonCoreArrowSpacing() {
        return navigationDragonCoreArrowSpacing;
    }

    public int getNavigationDragonCoreArrowMaxPoints() {
        return navigationDragonCoreArrowMaxPoints;
    }

    public int getNavigationDragonCoreArrowUpdateInterval() {
        return navigationDragonCoreArrowUpdateInterval;
    }

    public double getNavigationDragonCoreArrowRotationX() {
        return navigationDragonCoreArrowRotationX;
    }

    public double getNavigationDragonCoreArrowRotationYOffset() {
        return navigationDragonCoreArrowRotationYOffset;
    }

    public double getNavigationDragonCoreArrowRotationZ() {
        return navigationDragonCoreArrowRotationZ;
    }

    public double getNavigationDragonCoreArrowAlpha() {
        return navigationDragonCoreArrowAlpha;
    }

    public boolean isNavigationDragonCoreArrowThrough() {
        return navigationDragonCoreArrowThrough;
    }

    public boolean isNavigationDragonCoreArrowGlow() {
        return navigationDragonCoreArrowGlow;
    }

    public YamlConfiguration getHudYaml() {
        return hudYaml;
    }

    public Map<String, String> getHudVariableTextTemplates() {
        return hudVariableTextTemplates;
    }

    private Map<String, String> scanHudVariableTextTemplates(YamlConfiguration yaml) {
        if (yaml == null) {
            return Collections.emptyMap();
        }

        Map<String, String> templates = new LinkedHashMap<String, String>();
        scanSectionForVariableTexts(yaml, "", templates);
        return Collections.unmodifiableMap(templates);
    }

    private void scanSectionForVariableTexts(ConfigurationSection section, String path, Map<String, String> templates) {
        for (String key : section.getKeys(false)) {
            String childPath = path.isEmpty() ? key : path + "." + key;
            if ("texts".equals(key)) {
                String text = section.getString(key, "");
                if (containsInternalVariable(text) && hasText(path)) {
                    templates.put(path, Texts.color(text));
                }
                continue;
            }

            ConfigurationSection child = section.getConfigurationSection(key);
            if (child != null) {
                scanSectionForVariableTexts(child, childPath, templates);
            }
        }
    }

    private boolean containsInternalVariable(String text) {
        return text != null && text.contains("%xychemdahshow_");
    }

    private String getNavigationString(YamlConfiguration config, String hudPath, String configPath, String defaultValue) {
        String path = HUD_NAVIGATION_PATH + hudPath;
        if (hudYaml != null && hudYaml.contains(path)) {
            String value = hudYaml.getString(path, defaultValue);
            if (hasText(value)) {
                return value;
            }
        }
        return config.getString(configPath, defaultValue);
    }

    private double getNavigationDouble(YamlConfiguration config, String hudPath, String configPath, double defaultValue) {
        String path = HUD_NAVIGATION_PATH + hudPath;
        if (hudYaml != null && hudYaml.contains(path)) {
            return hudYaml.getDouble(path, defaultValue);
        }
        return config.getDouble(configPath, defaultValue);
    }

    private int getNavigationInt(YamlConfiguration config, String hudPath, String configPath, int defaultValue) {
        String path = HUD_NAVIGATION_PATH + hudPath;
        if (hudYaml != null && hudYaml.contains(path)) {
            return hudYaml.getInt(path, defaultValue);
        }
        return config.getInt(configPath, defaultValue);
    }

    private boolean getNavigationBoolean(YamlConfiguration config, String hudPath, String configPath, boolean defaultValue) {
        String path = HUD_NAVIGATION_PATH + hudPath;
        if (hudYaml != null && hudYaml.contains(path)) {
            return hudYaml.getBoolean(path, defaultValue);
        }
        return config.getBoolean(configPath, defaultValue);
    }

    private void stripPluginOnlyHudSettings() {
        if (hudYaml != null) {
            hudYaml.set("任务导航按钮.navigation", null);
        }
    }

    private boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
