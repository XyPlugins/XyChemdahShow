package com.xy.xychemdahshow.util;

import de.tr7zw.changeme.nbtapi.NBTItem;
import ink.ptms.chemdah.api.ChemdahAPI;
import ink.ptms.chemdah.core.PlayerProfile;
import ink.ptms.chemdah.core.quest.Quest;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.xy.xychemdahshow.XyChemdahShow;
import com.xy.xychemdahshow.manager.QuestManager;
import com.xy.xychemdahshow.pojo.UI;

import java.util.concurrent.ConcurrentHashMap;

public class InventoryUtil {

    private static boolean tipShown = false;

    public static void openQuestUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§c§lChemdah任务界面");

        ConcurrentHashMap<String, PlayerProfile> profileMap = ChemdahAPI.INSTANCE.getPlayerProfile();
        PlayerProfile profile = profileMap.get(player.getName());
        if (profile == null) return;

        ConcurrentHashMap<String, Quest> questMap = profile.getQuestMap();
        int slot = 0;

        for (UI ui : QuestManager.getUiList()) {
            String questId = ui.getQuestID();
            if (questMap.containsKey(questId)) {
                ItemStack item = new ItemStack(Material.CHEST);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(PlaceholderAPI.setPlaceholders(player, ui.getName()));
                    meta.setLore(PlaceholderAPI.setPlaceholders(player, ui.getText()));
                    item.setItemMeta(meta);
                }
                // 用NBT API存储任务ID
                NBTItem nbti = new NBTItem(item);
                nbti.setString("Quest", String.valueOf(QuestManager.getUiList().indexOf(ui)));
                item = nbti.getItem();

                if (slot >= 54) break;
                inv.setItem(slot++, item);
            }
        }

        if (slot > 27 && !tipShown) {
            XyChemdahShow.getInstance().getLogger().warning("本插件任务界面最多展示27个任务（翻页版本需额外购买），此提示仅显示一次。");
            tipShown = true;
        }

        player.openInventory(inv);
    }
}