package xy.xychemdahshow.nav;

import ink.ptms.chemdah.core.quest.Quest;
import ink.ptms.chemdah.core.quest.QuestContainer;
import ink.ptms.chemdah.core.quest.Template;
import ink.ptms.chemdah.core.quest.meta.Meta;
import ink.ptms.chemdah.core.quest.meta.MetaName;
import eos.moe.dragoncore.api.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xy.xychemdahshow.XyChemdahShow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class NavigationService {

    private final XyChemdahShow plugin;
    private final Map<UUID, NavigationTarget> navigatingPlayers = new HashMap<UUID, NavigationTarget>();
    private final Map<UUID, Integer> dragonCoreArrowCounts = new HashMap<UUID, Integer>();
    private final Map<UUID, List<ArrowRenderState>> dragonCoreArrowStates = new HashMap<UUID, List<ArrowRenderState>>();
    private BukkitTask task;
    private long taskInterval = -1L;

    public NavigationService(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    public void toggleNavigation(Player player) {
        if (player == null) {
            return;
        }
        if (!plugin.getSettings().isNavigationEnabled()) {
            XyChemdahShow.playerLog(player, "导航功能当前未启用");
            return;
        }

        UUID uniqueId = player.getUniqueId();
        if (navigatingPlayers.containsKey(uniqueId)) {
            stopNavigation(player);
            return;
        }

        NavigationTarget target = findFirstTarget(player);
        if (target == null) {
            XyChemdahShow.playerLog(player, "当前没有可导航的任务");
            return;
        }

        World world = Bukkit.getWorld(target.getWorld());
        if (world == null) {
            XyChemdahShow.playerLog(player, "导航目标世界不存在: " + target.getWorld());
            return;
        }
        if (!player.getWorld().equals(world)) {
            XyChemdahShow.playerLog(player, "请先传送到 " + target.getQuestName() + " &f后再开启导航");
            return;
        }

        navigatingPlayers.put(uniqueId, target);
        ensureTask();
        XyChemdahShow.playerLog(player, "正在导航: " + target.getQuestName());
    }

    public void stopNavigation(Player player) {
        stopNavigation(player, true);
    }

    public void stopNavigationSilently(Player player) {
        stopNavigation(player, false);
    }

    private void stopNavigation(Player player, boolean notify) {
        if (player == null) {
            return;
        }

        NavigationTarget removed = navigatingPlayers.remove(player.getUniqueId());
        if (removed != null) {
            clearDragonCoreArrows(player);
            if (notify) {
                XyChemdahShow.playerLog(player, "已停止导航");
            }
        }
        stopTaskIfIdle();
    }

    public boolean hasNavigationTarget(Player player, List<Quest> quests) {
        if (player == null || !plugin.getSettings().isNavigationEnabled()) {
            return false;
        }

        List<Quest> source = quests;
        if (source == null) {
            source = plugin.getChemdahBridge().getActiveQuests(player);
        }

        for (Quest quest : source) {
            if (getTarget(player, quest) != null) {
                return true;
            }
        }
        return false;
    }

    public void stopAll() {
        for (UUID uniqueId : new ArrayList<UUID>(dragonCoreArrowCounts.keySet())) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player != null && player.isOnline()) {
                clearDragonCoreArrows(player);
            }
        }
        navigatingPlayers.clear();
        dragonCoreArrowCounts.clear();
        dragonCoreArrowStates.clear();
        if (task != null) {
            task.cancel();
            task = null;
            taskInterval = -1L;
        }
    }

    public void refreshTaskInterval() {
        if (task == null || navigatingPlayers.isEmpty()) {
            return;
        }

        long interval = getTaskInterval();
        if (taskInterval == interval) {
            return;
        }

        task.cancel();
        task = null;
        taskInterval = -1L;
        ensureTask();
    }

    private void ensureTask() {
        if (task != null) {
            return;
        }

        long interval = getTaskInterval();
        task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                tick();
            }
        }, 1L, interval);
        taskInterval = interval;
    }

    private long getTaskInterval() {
        if (isDragonCoreArrowMode()) {
            return Math.max(1L, plugin.getSettings().getNavigationDragonCoreArrowUpdateInterval());
        }
        return Math.max(1L, plugin.getSettings().getNavigationParticleInterval());
    }

    private void stopTaskIfIdle() {
        if (!navigatingPlayers.isEmpty() || task == null) {
            return;
        }

        task.cancel();
        task = null;
        taskInterval = -1L;
    }

    private void tick() {
        if (navigatingPlayers.isEmpty()) {
            stopTaskIfIdle();
            return;
        }

        Iterator<Map.Entry<UUID, NavigationTarget>> iterator = navigatingPlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, NavigationTarget> entry = iterator.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                dragonCoreArrowCounts.remove(entry.getKey());
                dragonCoreArrowStates.remove(entry.getKey());
                iterator.remove();
                continue;
            }

            NavigationTarget target = entry.getValue();
            World world = Bukkit.getWorld(target.getWorld());
            if (world == null) {
                clearDragonCoreArrows(player);
                iterator.remove();
                XyChemdahShow.playerLog(player, "导航目标世界不存在: " + target.getWorld());
                continue;
            }
            if (!player.getWorld().equals(world)) {
                clearDragonCoreArrows(player);
                iterator.remove();
                XyChemdahShow.playerLog(player, "请先传送到 " + target.getQuestName() + " &f后再开启导航");
                continue;
            }

            Location playerLocation = player.getLocation();
            double dx = target.getX() - playerLocation.getX();
            double dz = target.getZ() - playerLocation.getZ();
            double arriveDistance = plugin.getSettings().getNavigationArriveDistance();
            if ((dx * dx + dz * dz) <= arriveDistance * arriveDistance) {
                iterator.remove();
                clearDragonCoreArrows(player);
                XyChemdahShow.playerLog(player, "已到达导航目标: " + target.getQuestName());
                continue;
            }

            drawRoute(player, dx, dz);
        }
        stopTaskIfIdle();
    }

    private void drawRoute(Player player, double dx, double dz) {
        if (isDragonCoreArrowMode()) {
            drawDragonCoreArrowRoute(player, dx, dz);
            return;
        }

        clearDragonCoreArrows(player);
        drawParticleRoute(player, dx, dz);
    }

    private boolean isDragonCoreArrowMode() {
        String mode = plugin.getSettings().getNavigationRenderMode();
        return mode != null && "dragoncore-arrow".equals(mode.trim().toLowerCase(Locale.US));
    }

    private void drawParticleRoute(Player player, double dx, double dz) {
        Vector direction = new Vector(dx, 0D, dz);
        double distanceToTarget = direction.length();
        if (distanceToTarget < 0.0001D) {
            return;
        }
        direction.normalize();

        double spacing = plugin.getSettings().getNavigationParticleSpacing();
        double maxLength = spacing * plugin.getSettings().getNavigationMaxPoints();
        double length = Math.min(distanceToTarget, maxLength);
        Location origin = player.getLocation().add(0D, plugin.getSettings().getNavigationGroundOffset(), 0D);
        Particle particle = getParticle();
        double referenceY = player.getLocation().getY();

        for (double distance = spacing; distance <= length; distance += spacing) {
            Location point = origin.clone().add(direction.clone().multiply(distance));
            point = snapToGround(point, referenceY);
            referenceY = point.getY();
            spawnParticle(player, particle, point);
        }

        Location head = origin.clone().add(direction.clone().multiply(length));
        head = snapToGround(head, referenceY);
        referenceY = head.getY();
        double headLength = plugin.getSettings().getNavigationArrowHeadLength();
        Vector left = rotate(direction, 145D).multiply(headLength);
        Vector right = rotate(direction, -145D).multiply(headLength);
        for (double distance = 0.0D; distance <= 1.0D; distance += 0.33D) {
            spawnParticle(player, particle, snapToGround(head.clone().add(left.clone().multiply(distance)), referenceY));
            spawnParticle(player, particle, snapToGround(head.clone().add(right.clone().multiply(distance)), referenceY));
        }
    }

    private void drawDragonCoreArrowRoute(Player player, double dx, double dz) {
        Vector direction = new Vector(dx, 0D, dz);
        double distanceToTarget = direction.length();
        if (distanceToTarget < 0.0001D) {
            clearDragonCoreArrows(player);
            return;
        }
        direction.normalize();

        double spacing = plugin.getSettings().getNavigationDragonCoreArrowSpacing();
        int maxPoints = plugin.getSettings().getNavigationDragonCoreArrowMaxPoints();
        int pointCount = Math.max(1, Math.min(maxPoints, (int) Math.ceil(distanceToTarget / spacing)));
        Location origin = player.getLocation().add(0D, plugin.getSettings().getNavigationGroundOffset(), 0D);
        double referenceY = player.getLocation().getY();
        float rotateX = (float) plugin.getSettings().getNavigationDragonCoreArrowRotationX();
        float rotateY = (float) (getRouteRotateY(direction) + plugin.getSettings().getNavigationDragonCoreArrowRotationYOffset());
        float rotateZ = (float) plugin.getSettings().getNavigationDragonCoreArrowRotationZ();
        String texture = plugin.getSettings().getNavigationDragonCoreArrowTexture();
        float width = (float) plugin.getSettings().getNavigationDragonCoreArrowWidth();
        float height = (float) plugin.getSettings().getNavigationDragonCoreArrowHeight();
        float alpha = (float) plugin.getSettings().getNavigationDragonCoreArrowAlpha();
        boolean through = plugin.getSettings().isNavigationDragonCoreArrowThrough();
        boolean glow = plugin.getSettings().isNavigationDragonCoreArrowGlow();
        List<ArrowRenderState> states = getArrowRenderStates(player);

        for (int index = 0; index < pointCount; index++) {
            double distance = Math.min(distanceToTarget, spacing * (index + 1));
            Location point = origin.clone().add(direction.clone().multiply(distance));
            point = snapToGround(point, referenceY);
            referenceY = point.getY();
            if (shouldUpdateArrow(states, index, point, rotateX, rotateY, rotateZ, texture, width, height, alpha, through, glow)) {
                CoreAPI.setPlayerWorldTexture(player, getDragonCoreArrowKey(index), point, rotateX, rotateY, rotateZ, texture, width, height, alpha, through, glow);
            }
        }

        clearExtraDragonCoreArrows(player, pointCount);
        dragonCoreArrowCounts.put(player.getUniqueId(), pointCount);
    }

    private List<ArrowRenderState> getArrowRenderStates(Player player) {
        UUID uniqueId = player.getUniqueId();
        List<ArrowRenderState> states = dragonCoreArrowStates.get(uniqueId);
        if (states == null) {
            states = new ArrayList<ArrowRenderState>();
            dragonCoreArrowStates.put(uniqueId, states);
        }
        return states;
    }

    private boolean shouldUpdateArrow(
            List<ArrowRenderState> states,
            int index,
            Location point,
            float rotateX,
            float rotateY,
            float rotateZ,
            String texture,
            float width,
            float height,
            float alpha,
            boolean through,
            boolean glow
    ) {
        while (states.size() <= index) {
            states.add(null);
        }

        ArrowRenderState next = new ArrowRenderState(point, rotateX, rotateY, rotateZ, texture, width, height, alpha, through, glow);
        ArrowRenderState previous = states.get(index);
        if (next.sameAs(previous)) {
            return false;
        }
        states.set(index, next);
        return true;
    }

    private double getRouteRotateY(Vector direction) {
        return Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));
    }

    private String getDragonCoreArrowKey(int index) {
        return "xychemdahshow_nav_arrow_" + index;
    }

    private void clearDragonCoreArrows(Player player) {
        if (player == null) {
            return;
        }

        Integer amount = dragonCoreArrowCounts.remove(player.getUniqueId());
        dragonCoreArrowStates.remove(player.getUniqueId());
        if (amount == null) {
            return;
        }
        for (int index = 0; index < amount; index++) {
            CoreAPI.removePlayerWorldTexture(player, getDragonCoreArrowKey(index));
        }
    }

    private void clearExtraDragonCoreArrows(Player player, int keepAmount) {
        Integer previousAmount = dragonCoreArrowCounts.get(player.getUniqueId());
        if (previousAmount == null || previousAmount <= keepAmount) {
            return;
        }
        for (int index = keepAmount; index < previousAmount; index++) {
            CoreAPI.removePlayerWorldTexture(player, getDragonCoreArrowKey(index));
        }
        List<ArrowRenderState> states = dragonCoreArrowStates.get(player.getUniqueId());
        if (states != null && states.size() > keepAmount) {
            states.subList(keepAmount, states.size()).clear();
        }
    }

    private Location snapToGround(Location location, double referenceY) {
        if (!plugin.getSettings().isNavigationGroundFollowEnabled()) {
            return location;
        }

        World world = location.getWorld();
        if (world == null) {
            return location;
        }

        int blockX = location.getBlockX();
        int blockZ = location.getBlockZ();
        if (!world.isChunkLoaded(blockX >> 4, blockZ >> 4)) {
            return location;
        }

        int maxY = Math.min(world.getMaxHeight() - 1, (int) Math.ceil(referenceY + plugin.getSettings().getNavigationGroundSearchUp()));
        int minY = Math.max(0, (int) Math.floor(referenceY - plugin.getSettings().getNavigationGroundSearchDown()));
        double bestY = Double.NaN;
        double bestDistance = Double.MAX_VALUE;
        for (int y = maxY; y >= minY; y--) {
            if (isWalkableSurface(world, blockX, y, blockZ)) {
                double surfaceY = y + 1D + plugin.getSettings().getNavigationGroundOffset();
                double distance = Math.abs(surfaceY - referenceY);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestY = surfaceY;
                }
            }
        }
        if (!Double.isNaN(bestY)) {
            location.setY(bestY);
        }
        return location;
    }

    private boolean isWalkableSurface(World world, int x, int y, int z) {
        if (!world.getBlockAt(x, y, z).getType().isSolid()) {
            return false;
        }

        int aboveY = y + 1;
        return aboveY >= world.getMaxHeight() || !world.getBlockAt(x, aboveY, z).getType().isSolid();
    }

    private void spawnParticle(Player player, Particle particle, Location location) {
        player.spawnParticle(particle, location, 1, 0D, 0D, 0D, 0D);
    }

    private Particle getParticle() {
        Particle preset = getPresetParticle(plugin.getSettings().getNavigationParticle());
        if (preset != null) {
            return preset;
        }

        try {
            return Particle.valueOf(plugin.getSettings().getNavigationParticle().toUpperCase(Locale.US));
        } catch (Exception ignored) {
            return Particle.VILLAGER_HAPPY;
        }
    }

    private Particle getPresetParticle(String name) {
        if (name == null) {
            return null;
        }

        String key = name.trim().toLowerCase(Locale.US).replace(" ", "").replace("_", "").replace("-", "");
        if (key.isEmpty()) {
            return null;
        }

        if ("绿色箭头".equals(key) || "绿色".equals(key) || "green".equals(key) || "happy".equals(key) || "villagerhappy".equals(key)) {
            return getParticleByName("VILLAGER_HAPPY");
        }
        if ("白色光点".equals(key) || "白光".equals(key) || "光点".equals(key) || "white".equals(key) || "endrod".equals(key)) {
            return getParticleByName("END_ROD");
        }
        if ("魔法紫".equals(key) || "紫色".equals(key) || "purple".equals(key) || "witch".equals(key) || "spellwitch".equals(key)) {
            return getParticleByName("SPELL_WITCH");
        }
        if ("蓝紫闪光".equals(key) || "魔法暴击".equals(key) || "critmagic".equals(key)) {
            return getParticleByName("CRIT_MAGIC");
        }
        if ("火焰".equals(key) || "flame".equals(key)) {
            return getParticleByName("FLAME");
        }
        if ("经验球".equals(key) || "经验球风格".equals(key) || "经验".equals(key) || "xp".equals(key) || "exp".equals(key) || "experience".equals(key)) {
            return getParticleByName("TOTEM");
        }
        return null;
    }

    private Particle getParticleByName(String name) {
        try {
            return Particle.valueOf(name);
        } catch (Exception ignored) {
            return Particle.VILLAGER_HAPPY;
        }
    }

    private Vector rotate(Vector vector, double degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x = vector.getX() * cos - vector.getZ() * sin;
        double z = vector.getX() * sin + vector.getZ() * cos;
        return new Vector(x, 0D, z);
    }

    private NavigationTarget findFirstTarget(Player player) {
        List<Quest> quests = plugin.getChemdahBridge().getActiveQuests(player);
        for (Quest quest : quests) {
            NavigationTarget target = getTarget(player, quest);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    private NavigationTarget getTarget(Player player, Quest quest) {
        if (quest == null) {
            return null;
        }

        Template template;
        try {
            template = quest.getTemplate();
        } catch (Throwable ignored) {
            template = null;
        }
        if (template == null) {
            return null;
        }

        Double x = getFirstConfigDouble(template, "addon.xychshow.nav.x", "addon.track.x");
        Double y = getFirstConfigDouble(template, "addon.xychshow.nav.y", "addon.track.y");
        Double z = getFirstConfigDouble(template, "addon.xychshow.nav.z", "addon.track.z");
        if (x == null || y == null || z == null) {
            return null;
        }

        String world = getFirstConfigString(template, "addon.xychshow.nav.world", "addon.track.world");
        if (world.trim().isEmpty()) {
            world = player.getWorld().getName();
        }

        String navigationName = getConfigString(template, "addon.xychshow.location");
        if (navigationName.trim().isEmpty()) {
            navigationName = getQuestDisplayName(quest);
        }
        navigationName = XyChemdahShow.color(navigationName);
        return new NavigationTarget(quest.getId(), navigationName, world, x, y, z);
    }

    private String getConfigString(QuestContainer container, String path) {
        Object value = getConfigValue(container, path);
        return value == null ? "" : String.valueOf(value);
    }

    private Object getConfigValue(QuestContainer container, String path) {
        try {
            Object config = QuestContainer.class.getMethod("getConfig").invoke(container);
            if (config == null) {
                return null;
            }

            Object value = config.getClass().getMethod("getString", String.class).invoke(config, path);
            if (value != null) {
                return value;
            }
        } catch (Throwable ignored) {
        }

        try {
            Object config = QuestContainer.class.getMethod("getConfig").invoke(container);
            if (config == null) {
                return null;
            }

            return config.getClass().getMethod("get", String.class).invoke(config, path);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private Double getConfigDouble(QuestContainer container, String path) {
        Object value = getConfigValue(container, path);
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        String text = String.valueOf(value);
        if (text.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(text.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getFirstConfigString(QuestContainer container, String firstPath, String secondPath) {
        String first = getConfigString(container, firstPath);
        if (!first.trim().isEmpty()) {
            return first;
        }
        return getConfigString(container, secondPath);
    }

    private Double getFirstConfigDouble(QuestContainer container, String firstPath, String secondPath) {
        Double first = getConfigDouble(container, firstPath);
        if (first != null) {
            return first;
        }
        return getConfigDouble(container, secondPath);
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

    private static final class ArrowRenderState {

        private static final double POSITION_EPSILON = 0.0001D;
        private static final float FLOAT_EPSILON = 0.0001F;

        private final String world;
        private final double x;
        private final double y;
        private final double z;
        private final float rotateX;
        private final float rotateY;
        private final float rotateZ;
        private final String texture;
        private final float width;
        private final float height;
        private final float alpha;
        private final boolean through;
        private final boolean glow;

        private ArrowRenderState(
                Location point,
                float rotateX,
                float rotateY,
                float rotateZ,
                String texture,
                float width,
                float height,
                float alpha,
                boolean through,
                boolean glow
        ) {
            this.world = point.getWorld() == null ? "" : point.getWorld().getName();
            this.x = point.getX();
            this.y = point.getY();
            this.z = point.getZ();
            this.rotateX = rotateX;
            this.rotateY = rotateY;
            this.rotateZ = rotateZ;
            this.texture = texture == null ? "" : texture;
            this.width = width;
            this.height = height;
            this.alpha = alpha;
            this.through = through;
            this.glow = glow;
        }

        private boolean sameAs(ArrowRenderState other) {
            return other != null
                    && world.equals(other.world)
                    && Math.abs(x - other.x) <= POSITION_EPSILON
                    && Math.abs(y - other.y) <= POSITION_EPSILON
                    && Math.abs(z - other.z) <= POSITION_EPSILON
                    && Math.abs(rotateX - other.rotateX) <= FLOAT_EPSILON
                    && Math.abs(rotateY - other.rotateY) <= FLOAT_EPSILON
                    && Math.abs(rotateZ - other.rotateZ) <= FLOAT_EPSILON
                    && texture.equals(other.texture)
                    && Math.abs(width - other.width) <= FLOAT_EPSILON
                    && Math.abs(height - other.height) <= FLOAT_EPSILON
                    && Math.abs(alpha - other.alpha) <= FLOAT_EPSILON
                    && through == other.through
                    && glow == other.glow;
        }
    }
}
