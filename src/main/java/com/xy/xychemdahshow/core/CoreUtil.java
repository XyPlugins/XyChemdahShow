package com.xy.xychemdahshow.core;

import eos.moe.dragoncore.network.PacketSender;
import ink.ptms.chemdah.api.ChemdahAPI;
import ink.ptms.chemdah.core.PlayerProfile;
import ink.ptms.chemdah.core.quest.Quest;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import com.xy.xychemdahshow.config.ConfigManager;
import com.xy.xychemdahshow.manager.QuestManager;
import com.xy.xychemdahshow.pojo.Hud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CoreUtil {

    public static void updateHud(Player player, boolean firstJoin) {
        ConcurrentHashMap<String, PlayerProfile> profileMap = ChemdahAPI.INSTANCE.getPlayerProfile();
        PlayerProfile profile = profileMap.get(player.getName());
        if (profile == null) return;

        ConcurrentHashMap<String, Quest> questMap = profile.getQuestMap();
        List<String> resultLines = new ArrayList<>();

        for (Hud hud : QuestManager.getHudList()) {
            String questId = hud.getQuestID();
            if (questMap.containsKey(questId)) {
                resultLines.add(hud.getName());
                resultLines.addAll(hud.getText());
            }
        }

        if (ConfigManager.isDeleteHud() && resultLines.isEmpty()) {
            closeHud(player);
            return;
        }

        // 首次打开需发送YAML
        if (firstJoin) {
            PacketSender.sendYaml(player, "Gui/questhud.yml", ConfigManager.getHudYaml());
        }
        // 打开HUD
        PacketSender.sendOpenHud(player, "questhud");

        // 更新文本
        String finalText = PlaceholderAPI.setPlaceholders(player, String.join("\n", resultLines));
        String function = "方法.设置组件值('任务信息_label','texts','" + finalText + "');";
        PacketSender.sendRunFunction(player, "questhud", function, false);
    }

    private static void closeHud(Player player) {
        PacketSender.sendRunFunction(player, "questhud", "方法.关闭HUD('questhud')", false);
    }
}