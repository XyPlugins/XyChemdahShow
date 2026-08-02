package xy.xychemdahshow.hud;

import eos.moe.dragoncore.network.PacketSender;
import ink.ptms.chemdah.core.Data;
import ink.ptms.chemdah.core.DataContainer;
import ink.ptms.chemdah.core.PlayerProfile;
import ink.ptms.chemdah.core.quest.Quest;
import ink.ptms.chemdah.core.quest.QuestContainer;
import ink.ptms.chemdah.core.quest.Task;
import ink.ptms.chemdah.core.quest.Template;
import ink.ptms.chemdah.core.quest.meta.Meta;
import ink.ptms.chemdah.core.quest.meta.MetaName;
import ink.ptms.chemdah.core.quest.objective.Objective;
import ink.ptms.chemdah.core.quest.objective.Progress;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xy.xychemdahshow.XyChemdahShow;
import xy.xychemdahshow.config.PluginSettings;
import xy.xychemdahshow.config.QuestView;
import xy.xychemdahshow.config.QuestViewRegistry;
import xy.xychemdahshow.hook.ChemdahBridge;
import xy.xychemdahshow.hook.PlaceholderBridge;
import xy.xychemdahshow.util.Texts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class HudService {

    private static final String HUD_NAME = "questhud";
    private static final String HUD_PATH = "Gui/questhud.yml";
    private static final String HUD_TEXT_COMPONENT = "任务信息_label";
    private static final String HUD_TITLE_COMPONENT = "任务标题_label";
    private static final String HUD_EMPTY_COMPONENT = "空任务_label";
    private static final String HUD_NAVIGATION_BUTTON_COMPONENT = "任务导航按钮";

    private final XyChemdahShow plugin;
    private final PluginSettings settings;
    private final QuestViewRegistry questViews;
    private final ChemdahBridge chemdahBridge;
    private final PlaceholderBridge placeholderBridge;
    private final Set<UUID> openedPlayers = new HashSet<UUID>();
    private BukkitTask keepAliveTask;
    private int keepAliveInterval = -1;
    private boolean keepAliveReopen;

    public HudService(
            XyChemdahShow plugin,
            PluginSettings settings,
            QuestViewRegistry questViews,
            ChemdahBridge chemdahBridge,
            PlaceholderBridge placeholderBridge
    ) {
        this.plugin = plugin;
        this.settings = settings;
        this.questViews = questViews;
        this.chemdahBridge = chemdahBridge;
        this.placeholderBridge = placeholderBridge;
    }

    public void refreshAll(boolean reopenHud) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateHud(player, reopenHud);
        }
    }

    public void updateHud(Player player, boolean reopenHud) {
        updateHud(player, reopenHud, reopenHud);
    }

    private void updateHud(Player player, boolean resendYaml, boolean forceOpenHud) {
        if (player == null || !player.isOnline()) {
            return;
        }

        List<Quest> activeQuests = chemdahBridge.getActiveQuests(player);
        Collections.sort(activeQuests, new Comparator<Quest>() {
            @Override
            public int compare(Quest left, Quest right) {
                return Integer.compare(getQuestWeight(left), getQuestWeight(right));
            }
        });

        List<String> lines = new ArrayList<String>();

        for (Quest quest : activeQuests) {
            if (quest == null) {
                continue;
            }

            QuestView view = questViews.getView(quest.getId());
            if (view == null) {
                addAutomaticQuestLines(lines, quest);
                continue;
            }

            lines.add(view.getName());
            lines.addAll(view.getText());
        }

        boolean hasTasks = !lines.isEmpty();
        boolean hasNavigationTarget = plugin.getNavigationService() != null
                && plugin.getNavigationService().hasNavigationTarget(player, activeQuests);
        if (!hasNavigationTarget && plugin.getNavigationService() != null) {
            plugin.getNavigationService().stopNavigationSilently(player);
        }

        if (!hasTasks && settings.isDeleteHud()) {
            if (openedPlayers.contains(player.getUniqueId())) {
                closeHud(player);
            }
            return;
        }

        if (forceOpenHud || !openedPlayers.contains(player.getUniqueId())) {
            openHud(player, resendYaml);
        }

        if (hasTitleComponent()) {
            String titleText = hasTasks ? lines.get(0) : "";
            String detailText = hasTasks ? (lines.size() > 1 ? Texts.joinLines(lines.subList(1, lines.size())) : "") : getEmptyFallbackText();
            setHudTitle(player, applyDisplayText(player, titleText, activeQuests));
            setHudText(player, applyDisplayText(player, detailText, activeQuests));
        } else {
            String text = hasTasks ? Texts.joinLines(lines) : getEmptyFallbackText();
            setHudText(player, applyDisplayText(player, text, activeQuests));
        }
        setEmptyText(player, hasTasks ? "" : applyDisplayText(player, settings.getEmptyText(), activeQuests));
        setEmptyTextVisible(player, !hasTasks);
        setNavigationButtonVisible(player, hasTasks && hasNavigationTarget);
        refreshVariableTextComponents(player, activeQuests);
    }

    public void refreshKeepAliveTask() {
        if (!settings.isHudKeepAliveEnabled()) {
            stopKeepAliveTask();
            return;
        }

        int interval = settings.getHudKeepAliveInterval();
        boolean reopen = settings.isHudKeepAliveReopen();
        if (keepAliveTask != null && keepAliveInterval == interval && keepAliveReopen == reopen) {
            return;
        }

        stopKeepAliveTask();
        keepAliveInterval = interval;
        keepAliveReopen = reopen;
        keepAliveTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                runKeepAlive();
            }
        }, interval, interval);
    }

    public void stopKeepAliveTask() {
        if (keepAliveTask != null) {
            keepAliveTask.cancel();
            keepAliveTask = null;
        }
        keepAliveInterval = -1;
        keepAliveReopen = false;
    }

    private void runKeepAlive() {
        plugin.getChemdahBridge().refreshProfiles();
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateHud(player, false, settings.isHudKeepAliveReopen());
        }
    }

    private void openHud(Player player, boolean sendYaml) {
        if (sendYaml) {
            PacketSender.sendYaml(player, HUD_PATH, settings.getHudYaml());
        }
        PacketSender.sendOpenHud(player, HUD_NAME);
        openedPlayers.add(player.getUniqueId());
    }

    private void closeHud(Player player) {
        PacketSender.sendRunFunction(player, HUD_NAME, "方法.关闭HUD('questhud');", false);
        openedPlayers.remove(player.getUniqueId());
    }

    private void setHudText(Player player, String text) {
        setComponentText(player, HUD_TEXT_COMPONENT, text);
    }

    private void setHudTitle(Player player, String text) {
        setComponentText(player, HUD_TITLE_COMPONENT, text);
    }

    private void setEmptyText(Player player, String text) {
        if (!hasEmptyComponent()) {
            return;
        }

        setComponentText(player, HUD_EMPTY_COMPONENT, text);
    }

    private void setEmptyTextVisible(Player player, boolean visible) {
        if (!hasEmptyComponent()) {
            return;
        }

        setComponentValue(player, HUD_EMPTY_COMPONENT, "visible", visible ? "界面变量.展开" : "false");
    }

    private void setNavigationButtonVisible(Player player, boolean visible) {
        if (!hasComponent(HUD_NAVIGATION_BUTTON_COMPONENT)) {
            return;
        }

        setComponentValue(player, HUD_NAVIGATION_BUTTON_COMPONENT, "visible", visible ? "界面变量.展开" : "false");
    }

    private String getEmptyFallbackText() {
        return hasEmptyComponent() ? "" : settings.getEmptyText();
    }

    private boolean hasEmptyComponent() {
        return hasComponent(HUD_EMPTY_COMPONENT);
    }

    private boolean hasTitleComponent() {
        return hasComponent(HUD_TITLE_COMPONENT);
    }

    private void refreshVariableTextComponents(Player player, List<Quest> activeQuests) {
        Map<String, String> templates = settings.getHudVariableTextTemplates();
        if (templates.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : templates.entrySet()) {
            String component = entry.getKey();
            if (!hasText(component) || HUD_TEXT_COMPONENT.equals(component) || HUD_TITLE_COMPONENT.equals(component)) {
                continue;
            }

            setComponentText(player, component, applyDisplayText(player, entry.getValue(), activeQuests));
        }
    }

    private String applyDisplayText(Player player, String source, List<Quest> activeQuests) {
        String text = applyInternalVariables(player, source, activeQuests);
        return placeholderBridge.apply(player, text);
    }

    private void setComponentText(Player player, String component, String text) {
        setComponentValue(player, component, "texts", text);
    }

    private void setComponentValue(Player player, String component, String key, String value) {
        String function = "方法.设置组件值('" + Texts.escapeDragonCoreString(component) + "','"
                + Texts.escapeDragonCoreString(key) + "','"
                + Texts.escapeDragonCoreString(value) + "');";
        PacketSender.sendRunFunction(player, HUD_NAME, function, false);
    }

    private boolean hasComponent(String component) {
        return settings.getHudYaml() != null && settings.getHudYaml().contains(component);
    }

    private String applyInternalVariables(Player player, String source, List<Quest> quests) {
        if (source == null) {
            return "";
        }

        return source
                .replace("%xychemdahshow_player%", player.getName())
                .replace("%xychemdahshow_task_amount%", String.valueOf(quests.size()))
                .replace("%xychemdahshow_task_names%", getQuestNames(quests))
                .replace("%xychemdahshow_completed_amount%", String.valueOf(countCompletedTasks(quests)));
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

    private int getQuestWeight(Quest quest) {
        if (quest == null) {
            return Integer.MAX_VALUE;
        }

        QuestView view = questViews.getView(quest.getId());
        return view == null ? Integer.MAX_VALUE : view.getWeight();
    }

    private void addAutomaticQuestLines(List<String> lines, Quest quest) {
        if (addXychShowQuestLines(lines, quest)) {
            return;
        }

        lines.add(Texts.color("&6" + getQuestDisplayName(quest)));

        List<Task> sortedTasks = getSortedTasks(quest);
        if (sortedTasks.isEmpty()) {
            return;
        }

        PlayerProfile profile = quest.getProfile();
        for (Task task : sortedTasks) {
            boolean completed = isTaskCompleted(profile, task);
            TaskProgress progress = getTaskProgress(profile, task, completed);
            String prefix = completed ? "&a- " : "&7- &f";
            String progressText = progress == null ? "" : formatTaskProgress(progress, completed);
            lines.add(Texts.color(prefix + getTaskDisplayName(task) + progressText));
        }
    }

    private boolean addXychShowQuestLines(List<String> lines, Quest quest) {
        Template template;
        try {
            template = quest.getTemplate();
        } catch (Throwable ignored) {
            template = null;
        }
        if (template == null) {
            return false;
        }

        String customType = getConfigText(template, "addon.xychshow.type");
        String location = getConfigText(template, "addon.xychshow.location");
        String target = getConfigText(template, "addon.xychshow.target");
        String detail = getConfigText(template, "addon.xychshow.detail");
        boolean enabled = hasText(customType) || hasText(location) || hasText(target) || hasText(detail);
        if (!enabled) {
            return false;
        }

        String type = firstText(customType, getContainerMetaText(template, "type"));
        lines.add(Texts.color("&6" + getQuestDisplayName(quest)));
        if (hasText(type)) {
            lines.add(formatStructuredLine(settings.getStructuredTypeLabel(), type));
        }
        if (hasText(location)) {
            lines.add(formatStructuredLine(settings.getStructuredLocationLabel(), location));
        }

        List<Task> sortedTasks = getSortedTasks(quest);
        PlayerProfile profile = quest.getProfile();
        if (hasText(target)) {
            String progressText = "";
            if (sortedTasks.size() == 1) {
                progressText = getTaskProgressText(profile, sortedTasks.get(0));
            }
            lines.add(formatStructuredLine(settings.getStructuredTargetLabel(), target + progressText));
        } else {
            for (Task task : sortedTasks) {
                lines.add(formatStructuredLine(settings.getStructuredTargetLabel(), getTaskDisplayName(task) + getTaskProgressText(profile, task)));
            }
        }


        if (hasText(detail)) {
            lines.add(formatStructuredLine(settings.getStructuredDetailLabel(), detail));
        }
        return true;
    }

    private String formatStructuredLine(String label, String value) {
        String safeLabel = label == null ? "" : label;
        String safeValue = value == null ? "" : value;
        if (!hasText(safeLabel)) {
            return Texts.color("&f" + safeValue);
        }

        return settings.getStructuredLineFormat()
                .replace("%label%", safeLabel)
                .replace("%value%", safeValue);
    }

    private List<Task> getSortedTasks(Quest quest) {
        Collection<Task> tasks = quest.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }

        List<Task> sortedTasks = new ArrayList<Task>(tasks);
        Collections.sort(sortedTasks, new Comparator<Task>() {
            @Override
            public int compare(Task left, Task right) {
                return left.getId().compareToIgnoreCase(right.getId());
            }
        });
        return sortedTasks;
    }

    private String getTaskProgressText(PlayerProfile profile, Task task) {
        boolean completed = isTaskCompleted(profile, task);
        TaskProgress progress = getTaskProgress(profile, task, completed);
        return progress == null ? "" : formatTaskProgress(progress, completed);
    }

    private boolean isTaskCompleted(PlayerProfile profile, Task task) {
        try {
            return profile != null && task.isCompleted(profile);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private TaskProgress getTaskProgress(PlayerProfile profile, Task task, boolean completed) {
        if (!settings.isTaskProgressEnabled() || profile == null || task == null) {
            return null;
        }

        TaskProgress progress = getObjectiveProgress(profile, task);
        if (progress == null) {
            progress = getStoredAmountProgress(profile, task);
        }
        if (progress == null) {
            return null;
        }

        double current = progress.current;
        double target = progress.target;
        if (completed && current < target) {
            current = target;
        }
        current = Math.max(0D, Math.min(current, target));
        return new TaskProgress(current, target);
    }

    private TaskProgress getObjectiveProgress(PlayerProfile profile, Task task) {
        try {
            Objective<?> objective = task.getObjective();
            if (objective == null) {
                return null;
            }

            Progress progress = objective.getProgress(profile, task);
            if (progress == null) {
                return null;
            }

            Double target = toNumber(progress.getTarget());
            if (target == null || target <= 0D) {
                return null;
            }

            Double current = toNumber(progress.getValue());
            return new TaskProgress(current == null ? 0D : current, target);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private TaskProgress getStoredAmountProgress(PlayerProfile profile, Task task) {
        try {
            DataContainer goal = task.getGoal();
            if (goal == null || !goal.containsKey("amount")) {
                return null;
            }

            Double target = toNumber(goal.get("amount"));
            if (target == null || target <= 0D) {
                return null;
            }

            Double current = toNumber(profile.dataOperator(task).get("amount", 0));
            return new TaskProgress(current == null ? 0D : current, target);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private String formatTaskProgress(TaskProgress progress, boolean completed) {
        String format = completed ? settings.getTaskCompletedProgressFormat() : settings.getTaskProgressFormat();
        double percent = progress.target <= 0D ? 0D : progress.current / progress.target * 100D;
        return format
                .replace("%current%", formatProgressNumber(progress.current))
                .replace("%target%", formatProgressNumber(progress.target))
                .replace("%percent%", formatProgressNumber(percent));
    }

    private String formatProgressNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.000001D) {
            return String.valueOf((long) Math.rint(value));
        }

        String text = String.format(Locale.US, "%.2f", value);
        while (text.contains(".") && text.endsWith("0")) {
            text = text.substring(0, text.length() - 1);
        }
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    private Double toNumber(Object value) {
        if (value == null) {
            return null;
        }

        Object raw = value;
        if (raw instanceof Data) {
            raw = ((Data) raw).getData();
        }
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        }

        try {
            return Double.parseDouble(String.valueOf(raw).trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getConfigText(QuestContainer container, String path) {
        try {
            Object config = QuestContainer.class.getMethod("getConfig").invoke(container);
            if (config == null) {
                return "";
            }

            Object value = config.getClass().getMethod("getString", String.class).invoke(config, path);
            if (value == null) {
                return "";
            }
            return normalizeInline(Texts.color(String.valueOf(value)));
        } catch (Throwable ignored) {
            return "";
        }
    }

    private String getContainerMetaText(QuestContainer container, String key) {
        try {
            Meta<?> meta = container.meta(key);
            if (meta instanceof MetaName) {
                String displayName = ((MetaName) meta).getDisplayName();
                if (hasText(displayName)) {
                    return displayName;
                }
            }
            if (meta != null && meta.getSource() != null) {
                return String.valueOf(meta.getSource());
            }
        } catch (Throwable ignored) {
        }
        return "";
    }

    private String normalizeInline(String text) {
        if (text == null) {
            return "";
        }

        String[] parts = text.replace('\r', '\n').split("\\n");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            String line = part.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(" §8/ §f");
            }
            builder.append(line);
        }
        return builder.toString();
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first : second;
    }

    private boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
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

    private String getTaskDisplayName(Task task) {
        return getContainerDisplayName(task, task.getId());
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

    private static final class TaskProgress {

        private final double current;
        private final double target;

        private TaskProgress(double current, double target) {
            this.current = current;
            this.target = target;
        }
    }
}
