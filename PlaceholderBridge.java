package xy.xychemdahshow;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import xy.xychemdahshow.command.MainCommand;
import xy.xychemdahshow.config.PluginSettings;
import xy.xychemdahshow.config.QuestViewRegistry;
import xy.xychemdahshow.hook.ChemdahBridge;
import xy.xychemdahshow.hook.PlaceholderBridge;
import xy.xychemdahshow.hud.HudService;
import xy.xychemdahshow.listener.PluginListener;

import java.io.File;

public final class XyChemdahShow extends JavaPlugin {

    private PluginSettings settings;
    private QuestViewRegistry questViews;
    private ChemdahBridge chemdahBridge;
    private PlaceholderBridge placeholderBridge;
    private HudService hudService;

    @Override
    public void onEnable() {
        saveDefaultFiles();

        this.settings = new PluginSettings(this);
        this.questViews = new QuestViewRegistry(this);
        this.chemdahBridge = new ChemdahBridge();
        this.placeholderBridge = new PlaceholderBridge(this);
        this.hudService = new HudService(this, settings, questViews, chemdahBridge, placeholderBridge);

        reloadInternal(false);

        MainCommand command = new MainCommand(this);
        getCommand("xychshow").setExecutor(command);
        getCommand("xychshow").setTabCompleter(command);
        Bukkit.getPluginManager().registerEvents(new PluginListener(this), this);

        log(Bukkit.getConsoleSender(), "轻量任务视图已启用");
    }

    @Override
    public void onDisable() {
        log(Bukkit.getConsoleSender(), "插件已卸载");
    }

    public void reloadInternal(boolean refreshOnlinePlayers) {
        settings.reload();
        questViews.reload();
        chemdahBridge.refreshProfiles();

        if (refreshOnlinePlayers) {
            hudService.refreshAll(true);
        }
    }

    public PluginSettings getSettings() {
        return settings;
    }

    public ChemdahBridge getChemdahBridge() {
        return chemdahBridge;
    }

    public HudService getHudService() {
        return hudService;
    }

    public static void log(CommandSender sender, String message) {
        sender.sendMessage("§a[XyChemdahShow] §f" + message);
    }

    private void saveDefaultFiles() {
        saveDefaultConfig();
        saveResourceIfMissing("questhud.yml");
        saveResourceIfMissing("Quest/任务配置.yml");
    }

    private void saveResourceIfMissing(String path) {
        File target = new File(getDataFolder(), path);
        if (target.exists()) {
            return;
        }
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        saveResource(path, false);
    }
}