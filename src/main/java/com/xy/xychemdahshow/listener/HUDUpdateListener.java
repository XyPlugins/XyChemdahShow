package com.xy.xychemdahshow.listener;

import ink.ptms.chemdah.api.event.collect.PluginReloadEvent;
import ink.ptms.chemdah.api.event.collect.QuestEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.xy.xychemdahshow.XyChemdahShow;
import com.xy.xychemdahshow.config.ConfigManager;
import com.xy.xychemdahshow.core.CoreUtil;
import com.xy.xychemdahshow.event.XyChemdahShowReloadEvent;

public class HUDUpdateListener implements Listener {

    private final XyChemdahShow plugin;

    public HUDUpdateListener(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            CoreUtil.updateHud(p, true);
        }, ConfigManager.getJoinDelay());
    }

    @EventHandler
    public void onQuestUpdate(QuestEvents.DataSet.Post event) {
        Player p = Bukkit.getPlayer(event.getPlayer());
        if (p != null && p.isOnline()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                CoreUtil.updateHud(p, false);
            }, ConfigManager.getHudDelay());
        }
    }

    @EventHandler
    public void onChemdahReload(PluginReloadEvent.Quest event) {
        // Chemdah任务重载后，重新加载任务配置并刷新所有在线玩家
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            new com.xy.xychemdahshow.config.QuestLoader().loadAll();
            for (Player p : Bukkit.getOnlinePlayers()) {
                CoreUtil.updateHud(p, false);
            }
        });
    }

    @EventHandler
    public void onPluginReload(XyChemdahShowReloadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // 重载配置
            ConfigManager.loadConfig(plugin);
            new com.xy.xychemdahshow.config.QuestLoader().loadAll();
            for (Player p : Bukkit.getOnlinePlayers()) {
                CoreUtil.updateHud(p, false);
            }
        });
    }
}