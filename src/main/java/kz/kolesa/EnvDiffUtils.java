package kz.kolesa;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvDiffUtils {

    public static Map<String, String> parseEnvFile(File file) throws Exception {
        Map<String, String> map = new HashMap<>();
        if (!file.exists()) return map;

        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }

        return map;
    }

    public static Set<String> loadIgnoreList(File file) throws Exception {
        Set<String> ignored = new HashSet<>();
        if (!file.exists()) return ignored;

        String json = new String(Files.readAllBytes(file.toPath()));
        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
        com.google.gson.JsonArray arr = obj.getAsJsonArray("DIFF_IGNORE");
        for (com.google.gson.JsonElement el : arr) {
            ignored.add(el.getAsString());
        }

        return ignored;
    }
}
