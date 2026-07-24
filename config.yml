package xy.xychemdahshow.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xy.xychemdahshow.XyChemdahShow;
import xy.xychemdahshow.reward.RewardParser;
import xy.xychemdahshow.util.Texts;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestViewRegistry {

    private final XyChemdahShow plugin;
    private final List<QuestView> views = new ArrayList<QuestView>();
    private final Map<String, QuestView> viewByQuestId = new HashMap<String, QuestView>();

    public QuestViewRegistry(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        views.clear();
        viewByQuestId.clear();

        File questFolder = new File(plugin.getDataFolder(), "Quest");
        if (!questFolder.exists() && !questFolder.mkdirs()) {
            XyChemdahShow.log(Bukkit.getConsoleSender(), "无法创建 Quest 配置目录");
            return;
        }

        List<File> files = new ArrayList<File>();
        collectYamlFiles(questFolder, files);

        for (File file : files) {
            loadFile(file);
        }

        Collections.sort(views, Comparator.comparingInt(QuestView::getWeight));
        XyChemdahShow.log(Bukkit.getConsoleSender(), "已加载 " + views.size() + " 个任务视图");
    }

    public List<QuestView> getViews() {
        return Collections.unmodifiableList(views);
    }

    public QuestView getView(String questId) {
        return viewByQuestId.get(questId);
    }

    private void loadFile(File file) {
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection root = yaml.getConfigurationSection("");
            if (root == null) {
                return;
            }

            for (String questId : root.getKeys(false)) {
                String name = Texts.color(yaml.getString(questId + ".hud.name", questId));
                int weight = yaml.getInt(questId + ".hud.weight", 1);
                List<String> text = Texts.color(yaml.getStringList(questId + ".hud.text"));
                List<String> rewardRaw = yaml.getStringList(questId + ".ui.reward");

                QuestView view = new QuestView(questId, name, weight, text, RewardParser.parse(rewardRaw));
                views.add(view);
                viewByQuestId.put(questId, view);
            }
        } catch (Exception exception) {
            XyChemdahShow.log(Bukkit.getConsoleSender(), "文件 " + file.getName() + " 配置存在异常");
            exception.printStackTrace();
        }
    }

    private void collectYamlFiles(File folder, List<File> files) {
        File[] children = folder.listFiles();
        if (children == null) {
            return;
        }

        for (File child : children) {
            if (child.isDirectory()) {
                collectYamlFiles(child, files);
                continue;
            }

            String name = child.getName().toLowerCase();
            if (name.endsWith(".yml") || name.endsWith(".yaml")) {
                files.add(child);
            }
        }
    }
}
