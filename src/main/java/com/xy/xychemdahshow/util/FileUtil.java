package com.xy.xychemdahshow.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<File> getAllYmlFiles(File dir) {
        List<File> result = new ArrayList<>();
        if (dir == null || !dir.exists()) return result;
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                result.addAll(getAllYmlFiles(f));
            } else if (f.getName().endsWith(".yml")) {
                result.add(f);
            }
        }
        return result;
    }
}