package xy.xychemdahshow.hook;

import ink.ptms.chemdah.api.ChemdahAPI;
import ink.ptms.chemdah.core.PlayerProfile;
import ink.ptms.chemdah.core.quest.Quest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ChemdahBridge {

    private ConcurrentHashMap<String, PlayerProfile> profiles = new ConcurrentHashMap<String, PlayerProfile>();

    public void refreshProfiles() {
        ConcurrentHashMap<String, PlayerProfile> currentProfiles = ChemdahAPI.INSTANCE.getPlayerProfile();
        profiles = currentProfiles == null ? new ConcurrentHashMap<String, PlayerProfile>() : currentProfiles;
    }

    public Set<String> getActiveQuestIds(Player player) {
        List<Quest> quests = getActiveQuests(player);
        if (quests.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> questIds = new HashSet<String>();
        for (Quest quest : quests) {
            questIds.add(quest.getId());
        }
        return questIds;
    }

    public List<Quest> getActiveQuests(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }

        PlayerProfile profile = profiles.get(player.getName());
        if (profile == null) {
            refreshProfiles();
            profile = profiles.get(player.getName());
        }

        if (profile == null || profile.getQuestMap() == null) {
            return Collections.emptyList();
        }

        ConcurrentHashMap<String, Quest> quests = profile.getQuestMap();
        List<Quest> activeQuests = new ArrayList<Quest>();
        for (Quest quest : quests.values()) {
            if (quest == null || !quest.isValid()) {
                continue;
            }
            activeQuests.add(quest);
        }
        return activeQuests;
    }
}
