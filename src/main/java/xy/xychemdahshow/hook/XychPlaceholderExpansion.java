package xy.xychemdahshow.hook;

import ink.ptms.chemdah.core.PlayerProfile;
import ink.ptms.chemdah.core.quest.Quest;
import ink.ptms.chemdah.core.quest.QuestContainer;
import ink.ptms.chemdah.core.quest.Task;
import ink.ptms.chemdah.core.quest.Template;
import ink.ptms.chemdah.core.quest.meta.Meta;
import ink.ptms.chemdah.core.quest.meta.MetaName;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xy.xychemdahshow.XyChemdahShow;

import java.util.List;

public final class XychPlaceholderExpansion extends PlaceholderExpansion {

    private final XyChemdahShow plugin;

    public XychPlaceholderExpansion(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "xychemdahshow";
    }

    @Override
    public String getAuthor() {
        return "Xy";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player instanceof Player) {
            return onPlaceholderRequest((Player) player, params);
        }
        if (player != null && player.getName() != null) {
            Player onlinePlayer = Bukkit.getPlayerExact(player.getName());
            if (onlinePlayer != null) {
                return onPlaceholderRequest(onlinePlayer, params);
            }
        }
        return "";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) {
            return "";
        }

        List<Quest> quests = plugin.getChemdahBridge().getActiveQuests(player);
        String key = params.toLowerCase();
        if ("player".equals(key)) {
            return player.getName();
        }
        if ("task_amount".equals(key)) {
            return String.valueOf(quests.size());
        }
        if ("task_names".equals(key)) {
            return getQuestNames(quests);
        }
        if ("completed_amount".equals(key)) {
            return String.valueOf(countCompletedTasks(quests));
        }
        return "";
    }

    private String getQuestNames(List<Quest> quests) {
        StringBuilder builder = new StringBuilder();
        for (Quest quest : quests) {
            if (quest == null) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(getQuestDisplayName(quest));
        }
        return builder.toString();
    }

    private int countCompletedTasks(List<Quest> quests) {
        int amount = 0;
        for (Quest quest : quests) {
            if (quest == null || quest.getTasks() == null) {
                continue;
            }

            PlayerProfile profile = quest.getProfile();
            for (Task task : quest.getTasks()) {
                try {
                    if (profile != null && task.isCompleted(profile)) {
                        amount++;
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return amount;
    }

    private String getQuestDisplayName(Quest quest) {
        String questId = quest.getId();
        try {
            Template template = quest.getTemplate();
            if (template == null) {
                return questId;
            }

            String displayName = getContainerDisplayName(template, questId);
            if (!displayName.trim().isEmpty()) {
                return displayName;
            }
        } catch (Throwable ignored) {
        }
        return questId;
    }

    private String getContainerDisplayName(QuestContainer container, String fallback) {
        try {
            Meta<?> meta = container.meta("name");
            if (meta instanceof MetaName) {
                String displayName = ((MetaName) meta).getDisplayName();
                if (displayName != null && !displayName.trim().isEmpty()) {
                    return displayName;
                }
            }

            if (meta != null && meta.getSource() != null) {
                String sourceName = String.valueOf(meta.getSource());
                if (!sourceName.trim().isEmpty()) {
                    return sourceName;
                }
            }
        } catch (Throwable ignored) {
        }
        return fallback;
    }
}