package com.lizardclient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ModuleLoader {
    private static final Gson GSON = new Gson();

    public static List<Module> loadModules() {
        List<Module> list = new ArrayList<>();
        Path dir = resolveModulesDir();
        if (dir == null) return list;
        try {
            if (!Files.isDirectory(dir)) return list;
            Files.list(dir)
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".json"))
                .forEach(p -> {
                    Module m = parseModule(p.toFile());
                    if (m != null) list.add(m);
                });
        } catch (Exception ignored) {}
        return list;
    }

    private static Module parseModule(File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if (obj == null) return null;
            Module m = new Module();
            m.id = obj.has("id") ? obj.get("id").getAsString() : file.getName();
            m.name = obj.has("name") ? obj.get("name").getAsString() : m.id;
            m.description = obj.has("description") ? obj.get("description").getAsString() : "";
            m.enabled = obj.has("enabled") && obj.get("enabled").getAsBoolean();
            m.keyCode = obj.has("key") ? obj.get("key").getAsInt() : 0;
            m.settings = parseSettings(obj.get("settings"));
            return m;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static List<ModuleSetting> parseSettings(JsonElement el) {
        List<ModuleSetting> out = new ArrayList<>();
        if (el == null || el.isJsonNull()) return out;
        if (!el.isJsonArray()) return out;
        JsonArray arr = el.getAsJsonArray();
        for (JsonElement e : arr) {
            if (!e.isJsonObject()) continue;
            JsonObject o = e.getAsJsonObject();
            String type = o.has("type") ? o.get("type").getAsString() : "";
            String id = o.has("id") ? o.get("id").getAsString() : "";
            String label = o.has("label") ? o.get("label").getAsString() : id;
            switch (type.toLowerCase()) {
                case "bool", "boolean" -> {
                    boolean val = o.has("value") && o.get("value").getAsBoolean();
                    out.add(new ModuleSetting.BoolSetting(id, label, val));
                }
                case "number", "double", "float", "int" -> {
                    double val = o.has("value") ? o.get("value").getAsDouble() : 0;
                    double min = o.has("min") ? o.get("min").getAsDouble() : 0;
                    double max = o.has("max") ? o.get("max").getAsDouble() : 100;
                    double step = o.has("step") ? o.get("step").getAsDouble() : 1;
                    out.add(new ModuleSetting.NumberSetting(id, label, val, min, max, step));
                }
            }
        }
        return out;
    }

    private static Path resolveModulesDir() {
        // Primary: /sdcard/Download/modules
        Path p1 = Path.of("/sdcard/Download/modules");
        if (Files.isDirectory(p1)) return p1;
        // Secondary: modules folder next to the game directory
        Path p2 = Path.of("modules");
        if (Files.isDirectory(p2)) return p2;
        return null;
    }
}
