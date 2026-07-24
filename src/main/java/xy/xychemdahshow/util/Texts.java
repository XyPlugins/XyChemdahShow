package xy.xychemdahshow.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class Texts {

    private Texts() {
    }

    public static String color(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(List<String> lines) {
        List<String> result = new ArrayList<String>();
        for (String line : lines) {
            result.add(color(line));
        }
        return result;
    }

    public static String joinLines(List<String> lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                builder.append('\n');
            }
            builder.append(lines.get(i));
        }
        return builder.toString();
    }

    public static String escapeDragonCoreString(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\").replace("'", "\\'");
    }
}
