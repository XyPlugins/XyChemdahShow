package com.xy.xychemdahshow;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.xy.xychemdahshow.command.MainCommand;
import com.xy.xychemdahshow.config.ConfigManager;
import com.xy.xychemdahshow.config.QuestLoader;
import com.xy.xychemdahshow.listener.HUDUpdateListener;
import com.xy.xychemdahshow.listener.InventoryListener;

public class XyChemdahShow extends JavaPlugin {

    private static XyChemdahShow instance;

    @Override
    public void onEnable() {
        instance = this;

        // 保存默认配置文件
        saveDefaultConfig();
        saveResource("questhud.yml", false);

        // 加载Quest任务配置
        new QuestLoader().loadAll();

        // 加载配置管理器
        ConfigManager.loadConfig(this);

        // 命令注册
        MainCommand mainCmd = new MainCommand();
        getCommand("xychemshow").setExecutor(mainCmd);
        getCommand("xychemshow").setTabCompleter(mainCmd);

        // 事件监听
        Bukkit.getPluginManager().registerEvents(new HUDUpdateListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);

        getLogger().info("XyChemdahShow 已启动！");
    }

    @Override
    public void onDisable() {
        getLogger().info("XyChemdahShow 已卸载");
    }

    public static XyChemdahShow getInstance() {
        return instance;
    }
}