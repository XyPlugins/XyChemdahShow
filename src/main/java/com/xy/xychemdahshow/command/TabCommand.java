package com.xy.xychemdahshow.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCommand {
    private static final List<String> SUB_COMMANDS = Arrays.asList("open", "reload");

    public static List<String> complete(String partial) {
        return SUB_COMMANDS.stream()
                .filter(s -> s.startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }
}