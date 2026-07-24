package xy.xychemdahshow.hook;

import org.bukkit.entity.Player;
import xy.xychemdahshow.XyChemdahShow;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class PlaceholderBridge {

    private final XyChemdahShow plugin;
    private Method setPlaceholders;
    private boolean searched;
    private Object internalExpansion;

    public PlaceholderBridge(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    public String apply(Player player, String text) {
        if (text == null || text.isEmpty() || !isAvailable()) {
            return text == null ? "" : text;
        }

        try {
            return (String) setPlaceholders.invoke(null, player, text);
        } catch (Exception ignored) {
            return text;
        }
    }

    public void registerInternalExpansion() {
        if (internalExpansion != null || !isAvailable()) {
            return;
        }

        try {
            Class<?> expansionClass = Class.forName("xy.xychemdahshow.hook.XychPlaceholderExpansion");
            Constructor<?> constructor = expansionClass.getConstructor(XyChemdahShow.class);
            Object expansion = constructor.newInstance(plugin);
            Boolean registered = (Boolean) expansionClass.getMethod("register").invoke(expansion);
            if (registered) {
                internalExpansion = expansion;
                XyChemdahShow.log(plugin.getServer().getConsoleSender(), "已注册 PlaceholderAPI 变量：%xychemdahshow_*%");
            }
        } catch (Throwable ignored) {
        }
    }

    public void unregisterInternalExpansion() {
        if (internalExpansion == null) {
            return;
        }

        try {
            internalExpansion.getClass().getMethod("unregister").invoke(internalExpansion);
        } catch (Exception ignored) {
        } finally {
            internalExpansion = null;
        }
    }

    private boolean isAvailable() {
        if (!searched) {
            searched = true;
            if (!plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                return false;
            }
            try {
                Class<?> placeholderApi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                setPlaceholders = placeholderApi.getMethod("setPlaceholders", Player.class, String.class);
            } catch (Exception ignored) {
                setPlaceholders = null;
            }
        }
        return setPlaceholders != null;
    }
}