package xy.xychemdahshow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xy.xychemdahshow.command.MainCommand;
import xy.xychemdahshow.config.PluginSettings;
import xy.xychemdahshow.config.QuestViewRegistry;
import xy.xychemdahshow.hook.ChemdahBridge;
import xy.xychemdahshow.hook.PlaceholderBridge;
import xy.xychemdahshow.hud.HudService;
import xy.xychemdahshow.listener.PluginListener;
import xy.xychemdahshow.nav.NavigationService;

import java.io.File;

public final class XyChemdahShow extends JavaPlugin {

    private static final String DEFAULT_LOCAL_PREFIX = "&7[&bXyChemdahShow&7]&r ";

    private PluginSettings settings;
    private QuestViewRegistry questViews;
    private ChemdahBridge chemdahBridge;
    private PlaceholderBridge placeholderBridge;
    private HudService hudService;
    private NavigationService navigationService;

    @Override
    public void onEnable() {
        saveDefaultFiles();

        this.settings = new PluginSettings(this);
        this.questViews = new QuestViewRegistry(this);
        this.chemdahBridge = new ChemdahBridge();
        this.placeholderBridge = new PlaceholderBridge(this);
        this.hudService = new HudService(this, settings, questViews, chemdahBridge, placeholderBridge);
        this.navigationService = new NavigationService(this);

        reloadInternal(false);
        placeholderBridge.registerInternalExpansion();

        MainCommand command = new MainCommand(this);
        getCommand("xychshow").setExecutor(command);
        getCommand("xychshow").setTabCompleter(command);
        Bukkit.getPluginManager().registerEvents(new PluginListener(this), this);

        log(Bukkit.getConsoleSender(), "轻量任务视图已启用");
    }

    @Override
    public void onDisable() {
        if (placeholderBridge != null) {
            placeholderBridge.unregisterInternalExpansion();
        }
        if (navigationService != null) {
            navigationService.stopAll();
        }
        log(Bukkit.getConsoleSender(), "插件已卸载");
    }

    public void reloadInternal(boolean refreshOnlinePlayers) {
        settings.reload();
        questViews.reload();
        chemdahBridge.refreshProfiles();
        if (navigationService != null) {
            navigationService.refreshTaskInterval();
        }

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

    public NavigationService getNavigationService() {
        return navigationService;
    }

    public static void log(CommandSender sender, String message) {
        String prefix = sender instanceof Player ? getUnifiedPrefix() : getLocalPrefix();
        sender.sendMessage(color(prefix + "&f" + message));
    }

    public static String getMessagePrefix() {
        return color(getUnifiedPrefix());
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }

    private static String getUnifiedPrefix() {
        String prefix = getXyCorePrefix();
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = getLocalPrefix();
        }
        return prefix;
    }

    private static String getXyCorePrefix() {
        Plugin core = Bukkit.getPluginManager().getPlugin("XyCore");
        if (core == null || !core.isEnabled()) {
            return null;
        }
        try {
            ClassLoader loader = core.getClass().getClassLoader();
            Class<?> entry = Class.forName("org.xyplugin.xycore.api.XyCore", true, loader);
            Object api = entry.getMethod("get").invoke(null);
            Object prefix = api.getClass().getMethod("getMessagePrefix").invoke(api);
            return prefix == null ? "" : String.valueOf(prefix);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String getLocalPrefix() {
        try {
            return JavaPlugin.getPlugin(XyChemdahShow.class).getConfig()
                    .getString("messages.prefix", DEFAULT_LOCAL_PREFIX);
        } catch (IllegalStateException ignored) {
            return DEFAULT_LOCAL_PREFIX;
        }
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
