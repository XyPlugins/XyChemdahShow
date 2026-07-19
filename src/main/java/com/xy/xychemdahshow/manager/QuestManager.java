package com.xy.xychemdahshow.manager;

import com.xy.xychemdahshow.pojo.Hud;
import com.xy.xychemdahshow.pojo.UI;
import java.util.*;

public class QuestManager {

    private static Map<String, Hud> hudMap = new HashMap<>();
    private static Map<String, UI> uiMap = new HashMap<>();
    private static List<Hud> hudList = new ArrayList<>();
    private static List<UI> uiList = new ArrayList<>();

    public static void init() {
        hudMap.clear();
        uiMap.clear();
        hudList.clear();
        uiList.clear();
    }

    public static Map<String, Hud> getHudMap() { return hudMap; }
    public static Map<String, UI> getUiMap() { return uiMap; }
    public static List<Hud> getHudList() { return hudList; }
    public static List<UI> getUiList() { return uiList; }

    public static void sortAll() {
        hudList = new ArrayList<>(hudMap.values());
        hudList.sort(Comparator.comparingInt(Hud::getWeight));
        uiList = new ArrayList<>(uiMap.values());
        uiList.sort(Comparator.comparingInt(UI::getWeight));
    }
}