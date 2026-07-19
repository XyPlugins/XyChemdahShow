package com.xy.xychemdahshow.config;

import org.bukkit.configuration.file.YamlConfiguration;
import com.xy.xychemdahshow.XyChemdahShow;
import java.io.File;

public class ConfigManager {
    private static int hudDelay;
    private static int joinDelay;
    private static boolean deleteHud;
    private static YamlConfiguration hudYaml;

    public static void loadConfig(XyChemdahShow plugin) {
        plugin.reloadConfig();
        hudDelay = plugin.getConfig().getInt("huddelay", 20);
        joinDelay = plugin.getConfig().getInt("joindelay", 20);
        deleteHud = plugin.getConfig().getBoolean("deletehud", false);

        File hudFile = new File(plugin.getDataFolder(), "questhud.yml");
        if (!hudFile.exists()) {
            plugin.saveResource("questhud.yml", false);
        }
        hudYaml = YamlConfiguration.loadConfiguration(hudFile);
    }

    public static int getHudDelay() { return hudDelay; }
    public static int getJoinDelay() { return joinDelay; }
    public static boolean isDeleteHud() { return deleteHud; }
    public static YamlConfiguration getHudYaml() { return hudYaml; }
}