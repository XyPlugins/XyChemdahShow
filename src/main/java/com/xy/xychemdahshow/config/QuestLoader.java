package com.xy.xychemdahshow.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import com.xy.xychemdahshow.XyChemdahShow;
import com.xy.xychemdahshow.manager.QuestManager;
import com.xy.xychemdahshow.pojo.Hud;
import com.xy.xychemdahshow.pojo.UI;
import com.xy.xychemdahshow.util.FileUtil;

import java.io.File;
import java.util.List;

public class QuestLoader {

    public void loadAll() {
        File questFolder = new File(XyChemdahShow.getInstance().getDataFolder(), "Quest");
        if (!questFolder.exists()) {
            questFolder.mkdirs();
            // 可在此保存示例配置文件
        }

        List<File> files = FileUtil.getAllYmlFiles(questFolder);
        QuestManager.init();

        for (File file : files) {
            try {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                for (String questID : yaml.getKeys(false)) {
                    // Hud
                    String hudName = colorize(yaml.getString(questID + ".hud.name", ""));
                    int hudWeight = yaml.getInt(questID + ".hud.weight", 1);
                    List<String> hudText = colorizeList(yaml.getStringList(questID + ".hud.text"));

                    // UI
                    String uiName = colorize(yaml.getString(questID + ".ui.name", ""));
                    int uiWeight = yaml.getInt(questID + ".ui.weight", 1);
                    List<String> uiText = colorizeList(yaml.getStringList(questID + ".ui.text"));
                    List<String> reward = yaml.getStringList(questID + ".ui.reward");

                    QuestManager.getHudMap().put(questID, new Hud(questID, hudName, hudWeight, hudText));
                    QuestManager.getUiMap().put(questID, new UI(questID, uiName, uiWeight, uiText, reward));
                }
            } catch (Exception e) {
                XyChemdahShow.getInstance().getLogger().warning("加载任务文件失败: " + file.getName());
                e.printStackTrace();
            }
        }
        QuestManager.sortAll();
    }

    private String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private List<String> colorizeList(List<String> list) {
        list.replaceAll(this::colorize);
        return list;
    }
}