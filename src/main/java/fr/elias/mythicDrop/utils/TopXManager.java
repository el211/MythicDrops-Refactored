package fr.elias.mythicDrop.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.elias.mythicDrop.utils.ConfigLookup.listContainsIgnoreCase;

public class TopXManager {

    private final File dataFolder;
    private final Map<Integer, YamlConfiguration> configs = new HashMap<>();
    private final Map<String, Integer> mobToTopX = new HashMap<>();

    private static final Pattern FILENAME_PATTERN = Pattern.compile("top(\\d+)damage\\.yml");

    public TopXManager(File dataFolder) {
        this.dataFolder = dataFolder;
        load();
    }

    public void load() {
        File[] files = dataFolder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (!file.isFile()) continue;
            String name = file.getName();

            if (!name.matches("top\\d+damage\\.yml")) continue;
            if (name.equals("top3damage.yml")) continue;
            if (name.equals("top5damage.yml")) continue;

            Matcher matcher = FILENAME_PATTERN.matcher(name);
            if (!matcher.matches()) continue;

            int topXFromFile = Integer.parseInt(matcher.group(1));

            YamlConfiguration config;
            try {
                config = YamlConfiguration.loadConfiguration(file);
            } catch (Exception e) {
                MythicLogger.warn("Failed to load topX config file: " + name + " — " + e.getMessage());
                continue;
            }

            // Allow overriding the topX value via the file's topX: key
            int topX = config.getInt("topX", topXFromFile);

            configs.put(topX, config);

            // Index mobs listed under "rewardtopX"
            List<String> mobList = config.getStringList("rewardtopX");
            for (String mob : mobList) {
                if (mob != null) {
                    mobToTopX.put(mob.toLowerCase(Locale.ROOT), topX);
                }
            }
        }
    }

    public void reload() {
        configs.clear();
        mobToTopX.clear();
        load();
    }

    public Optional<Map.Entry<Integer, YamlConfiguration>> getConfigForMob(String mobName) {
        if (mobName == null) return Optional.empty();

        for (Map.Entry<Integer, YamlConfiguration> entry : configs.entrySet()) {
            YamlConfiguration config = entry.getValue();
            List<String> mobList = config.getStringList("rewardtopX");
            if (listContainsIgnoreCase(mobList, mobName)) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

    public Map<Integer, YamlConfiguration> getConfigs() {
        return Collections.unmodifiableMap(configs);
    }
}
