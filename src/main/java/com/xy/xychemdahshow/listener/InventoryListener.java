package com.xy.xychemdahshow.listener;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.xy.xychemdahshow.XyChemdahShow;
import com.xy.xychemdahshow.manager.QuestManager;
import com.xy.xychemdahshow.pojo.UI;

import java.util.Arrays;
import java.util.List;

public class InventoryListener implements Listener {

    private static final List<Integer> REWARD_SLOTS = Arrays.asList(
            36,37,38,39,40,41,42,43,44,
            45,46,47,48,49,50,51,52,53
    );

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTitle().equals("§c§lChemdah任务界面")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§c§lChemdah任务界面")) return;
        e.setCancelled(true);

        if (e.getClick().isShiftClick() || e.getClick().isKeyboardClick()) return;

        int slot = e.getRawSlot();
        if (slot < 0 || slot >= 54) return;
        ItemStack clicked = e.getInventory().getItem(slot);
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) e.getWhoClicked();
        // 读取NBT中存储的任务索引
        NBTItem nbti = new NBTItem(clicked);
        String questIndexStr = nbti.getString("Quest");
        if (questIndexStr == null || questIndexStr.isEmpty()) return;

        int index = Integer.parseInt(questIndexStr);
        if (index >= QuestManager.getUiList().size()) return;

        UI ui = QuestManager.getUiList().get(index);
        // 显示奖励物品（异步构造）
        Bukkit.getScheduler().runTaskAsynchronously(XyChemdahShow.getInstance(), () -> {
            // 清空奖励槽（36~53）
            for (int i = 36; i <= 53; i++) {
                e.getInventory().setItem(i, null);
            }
            // 根据配置显示奖励
            List<String> rewards = ui.getReward();
            int rewardSlot = 0;
            for (String rewardStr : rewards) {
                ItemStack rewardItem = parseReward(rewardStr);
                if (rewardItem != null && rewardSlot < REWARD_SLOTS.size()) {
                    e.getInventory().setItem(REWARD_SLOTS.get(rewardSlot++), rewardItem);
                }
            }
        });
    }

    private ItemStack parseReward(String rewardStr) {
        // 简易解析，仅支持普通物品和[mm]与[rm]（如需可扩展）
        String[] parts = rewardStr.split(":");
        if (parts.length < 1) return null;

        // 如果以[mm]开头，尝试从MythicMobs获取（需要检测MM是否存在，这里简化）
        if (parts[0].equalsIgnoreCase("[mm]") && parts.length >= 2) {
            // 这里需要MM API，若未加载则返回null
            return null;
        }
        // 如果是[rm]（RedmiItem），我们不再支持，返回null
        if (parts[0].equalsIgnoreCase("[rm]")) {
            return null;
        }
        // 否则尝试作为普通物品（仅演示，实际可扩展）
        Material mat = Material.getMaterial(parts[0].toUpperCase());
        if (mat == null) return null;
        ItemStack item = new ItemStack(mat);
        if (parts.length >= 3) {
            try {
                item.setAmount(Integer.parseInt(parts[2]));
            } catch (NumberFormatException ignored) {}
        }
        // 可能还有名字或lore，此处忽略
        return item;
    }
}