package xy.xychemdahshow.listener;

import ink.ptms.chemdah.api.event.collect.PluginReloadEvent;
import ink.ptms.chemdah.api.event.collect.ObjectiveEvents;
import ink.ptms.chemdah.api.event.collect.PlayerEvents;
import ink.ptms.chemdah.api.event.collect.QuestEvents;
import ink.ptms.chemdah.core.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xy.xychemdahshow.XyChemdahShow;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginListener implements Listener {

    private final XyChemdahShow plugin;
    private final Set<UUID> scheduledRefreshPlayers = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
    private final Set<UUID> scheduledProgressRefreshPlayers = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());

    public PluginListener(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getChemdahBridge().refreshProfiles();
            plugin.getHudService().updateHud(player, true);
        }, plugin.getSettings().getJoinDelay());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        scheduledRefreshPlayers.remove(uniqueId);
        scheduledProgressRefreshPlayers.remove(uniqueId);
        plugin.getHudService().forgetPlayer(player);
        plugin.getNavigationService().stopNavigationSilently(player);
    }

    @EventHandler
    public void onQuestDataPost(QuestEvents.DataSet.Post event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onQuestDataRemovePost(QuestEvents.DataRemove.Post event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onQuestAcceptPost(QuestEvents.Accept.Post event) {
        scheduleRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onQuestRegistered(QuestEvents.Registered event) {
        scheduleRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onQuestCompletePost(QuestEvents.Complete.Post event) {
        scheduleRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onQuestFailPost(QuestEvents.Fail.Post event) {
        scheduleRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onQuestRestartPost(QuestEvents.Restart.Post event) {
        scheduleRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onQuestUnregistered(QuestEvents.Unregistered event) {
        scheduleRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onObjectiveContinuePost(ObjectiveEvents.Continue.Post event) {
        scheduleProgressRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onObjectiveCompletePost(ObjectiveEvents.Complete.Post event) {
        scheduleProgressRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onObjectiveRestartPost(ObjectiveEvents.Restart.Post event) {
        scheduleProgressRefresh(event.getPlayerProfile());
    }

    @EventHandler
    public void onPlayerSelected(PlayerEvents.Selected event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onPlayerUpdated(PlayerEvents.Updated event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDataSetPost(PlayerEvents.DataSet.Post event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDataRemovePost(PlayerEvents.DataRemove.Post event) {
        scheduleRefresh(event.getPlayer());
    }

    @EventHandler
    public void onChemdahQuestReload(PluginReloadEvent.Quest event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.reloadInternal(false);
            plugin.getHudService().refreshAll(false);
            XyChemdahShow.log(Bukkit.getConsoleSender(), "检测到 Chemdah 任务重载，已同步重载任务视图");
        }, 40L);
    }

    private void scheduleRefresh(PlayerProfile profile) {
        if (profile == null) {
            return;
        }
        scheduleRefresh(profile.getPlayer());
    }

    private void scheduleProgressRefresh(PlayerProfile profile) {
        if (profile == null) {
            return;
        }
        schedulePlayerRefresh(profile.getPlayer(), plugin.getSettings().getProgressRefreshDelay(), scheduledProgressRefreshPlayers);
    }

    private void scheduleRefresh(final Player player) {
        schedulePlayerRefresh(player, plugin.getSettings().getHudDelay(), scheduledRefreshPlayers);
    }

    private void schedulePlayerRefresh(final Player player, long delay, final Set<UUID> scheduledPlayers) {
        if (player == null) {
            return;
        }

        final UUID uniqueId = player.getUniqueId();
        if (!scheduledPlayers.add(uniqueId)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                plugin.getChemdahBridge().refreshProfiles();
                plugin.getHudService().updateHud(player, false);
            } finally {
                scheduledPlayers.remove(uniqueId);
            }
        }, delay);
    }
}
