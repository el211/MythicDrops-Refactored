package fr.elias.mythicDrop.quests;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.utils.MythicLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Serializes {@link QuestManager}'s player progress and completed sets
 * to {@code quest_data.yml} on shutdown, and restores them on startup.
 */
public class QuestDataPersistence {

    private final QuestManager questManager;
    private final File dataFile;

    public QuestDataPersistence(MythicDrop plugin, QuestManager questManager) {
        this.questManager = questManager;
        this.dataFile = new File(plugin.getDataFolder(), "quest_data.yml");
        load();
    }

    // -------------------------------------------------------------------------
    // Load
    // -------------------------------------------------------------------------

    public void load() {
        if (!dataFile.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);

        // Restore kill progress
        ConfigurationSection progressSec = yaml.getConfigurationSection("progress");
        if (progressSec != null) {
            for (String uuidStr : progressSec.getKeys(false)) {
                UUID uuid = parseUUID(uuidStr);
                if (uuid == null) continue;

                ConfigurationSection playerSec = progressSec.getConfigurationSection(uuidStr);
                if (playerSec == null) continue;

                Map<String, Integer> progress = new HashMap<>();
                for (String questID : playerSec.getKeys(false)) {
                    progress.put(questID, playerSec.getInt(questID, 0));
                }
                questManager.getPlayerProgress().put(uuid, progress);
            }
        }

        // Restore completed quests
        ConfigurationSection completedSec = yaml.getConfigurationSection("completed");
        if (completedSec != null) {
            for (String uuidStr : completedSec.getKeys(false)) {
                UUID uuid = parseUUID(uuidStr);
                if (uuid == null) continue;

                List<String> list = completedSec.getStringList(uuidStr);
                questManager.getCompletedQuests().put(uuid, new HashSet<>(list));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Save
    // -------------------------------------------------------------------------

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();

        // Persist kill progress (only non-zero entries)
        for (Map.Entry<UUID, Map<String, Integer>> entry : questManager.getPlayerProgress().entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, Integer> q : entry.getValue().entrySet()) {
                if (q.getValue() > 0) {
                    yaml.set("progress." + uuidStr + "." + q.getKey(), q.getValue());
                }
            }
        }

        // Persist completed quest IDs
        for (Map.Entry<UUID, Set<String>> entry : questManager.getCompletedQuests().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                yaml.set("completed." + entry.getKey().toString(),
                        new ArrayList<>(entry.getValue()));
            }
        }

        try {
            dataFile.getParentFile().mkdirs();
            yaml.save(dataFile);
        } catch (IOException e) {
            MythicLogger.severe("Failed to save quest_data.yml: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private UUID parseUUID(String s) {
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            MythicLogger.warn("quest_data.yml: skipping invalid UUID '" + s + "'");
            return null;
        }
    }
}
