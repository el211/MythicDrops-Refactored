package fr.elias.mythicDrop.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

/**
 * Reads the optional {@code mythicdrop-topx} block that can be embedded
 * directly inside a MythicMobs mob YAML file.
 *
 * Example inside plugins/MythicMobs/mobs/DragonBoss.yml:
 * <pre>
 * DragonBoss:
 *   Type: ENDER_DRAGON
 *   Health: 5000
 *   # ... other MythicMobs keys ...
 *
 *   mythicdrop-topx:
 *     topX: 7
 *     rewardtopX-settings:
 *       use-flexible-rewards: true
 *     use-standard-rewards: false
 *     guaranteedperrank: true
 *     per-rank-group: true
 *     guaranteed-rewards:
 *       default: 1
 *       vip: 2
 *     position-1:
 *       guaranteed-rewards: 1
 *       default:
 *         drop1:
 *           command: "give %player% netherite_ingot 1"
 *           chance: 1.0
 *           message: "&6#1 Damage Dealer!"
 *     # ... position-2 through position-7 ...
 *     everyone-else-who-contributed:
 *       min-damage: 100.0
 *       default:
 *         drop1:
 *           command: "give %player% coal 8"
 *           chance: 0.4
 *           message: "&7Thanks for contributing!"
 * </pre>
 */
public class MythicMobConfigReader {

    private static final String TOPX_KEY = "mythicdrop-topx";

    // Cache: mobName -> result (Optional.empty() = checked, not found)
    private static final Map<String, Optional<Map.Entry<Integer, YamlConfiguration>>> cache = new HashMap<>();

    /**
     * Scans the MythicMobs mobs directory for the given mob and returns a
     * synthetic {@link YamlConfiguration} compatible with
     * {@link fr.elias.mythicDrop.handlers.TopXRewardsHandler} if a
     * {@code mythicdrop-topx} block is present.
     */
    public static Optional<Map.Entry<Integer, YamlConfiguration>> getTopXFromMobConfig(String mobName) {
        if (mobName == null) return Optional.empty();

        String key = mobName.toLowerCase(Locale.ROOT);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mythicMobs == null) {
            cache.put(key, Optional.empty());
            return Optional.empty();
        }

        File mobsDir = new File(mythicMobs.getDataFolder(), "mobs");
        if (!mobsDir.exists() || !mobsDir.isDirectory()) {
            cache.put(key, Optional.empty());
            return Optional.empty();
        }

        Optional<Map.Entry<Integer, YamlConfiguration>> result = scanDir(mobsDir, mobName);
        cache.put(key, result);
        return result;
    }

    /** Clears the internal cache — call this on plugin reload. */
    public static void clearCache() {
        cache.clear();
    }

    // -------------------------------------------------------------------------

    private static Optional<Map.Entry<Integer, YamlConfiguration>> scanDir(File dir, String mobName) {
        File[] files = dir.listFiles();
        if (files == null) return Optional.empty();

        for (File file : files) {
            if (file.isDirectory()) {
                Optional<Map.Entry<Integer, YamlConfiguration>> sub = scanDir(file, mobName);
                if (sub.isPresent()) return sub;
                continue;
            }

            String name = file.getName();
            if (!name.endsWith(".yml") && !name.endsWith(".yaml")) continue;

            YamlConfiguration mobFile;
            try {
                mobFile = YamlConfiguration.loadConfiguration(file);
            } catch (Exception e) {
                logDebug("MythicMobConfigReader: failed to load " + file.getName() + " — " + e.getMessage());
                continue;
            }

            ConfigurationSection mobSection = getSectionIgnoreCase(mobFile, mobName);
            if (mobSection == null) continue;

            ConfigurationSection topXSection = mobSection.getConfigurationSection(TOPX_KEY);
            if (topXSection == null) continue;

            logDebug("MythicMobConfigReader: found " + TOPX_KEY + " for mob " + mobName + " in " + file.getName());
            return Optional.of(buildSyntheticConfig(mobName, topXSection));
        }

        return Optional.empty();
    }

    /**
     * Converts the {@code mythicdrop-topx} section into a YamlConfiguration
     * whose layout matches what {@code TopXRewardsHandler} expects:
     *
     * <pre>
     * rewardtopX:
     *   - &lt;mobName&gt;
     * rewardtopX-settings:
     *   ...
     * &lt;mobName&gt;:
     *   use-standard-rewards: ...
     *   position-1: ...
     *   position-2: ...
     *   everyone-else-who-contributed: ...
     * </pre>
     */
    private static Map.Entry<Integer, YamlConfiguration> buildSyntheticConfig(
            String mobName, ConfigurationSection topXSection) {

        int topX = topXSection.getInt("topX", 3);
        YamlConfiguration synth = new YamlConfiguration();

        // Required by TopXRewardsHandler
        synth.set("rewardtopX", Collections.singletonList(mobName));

        // Optional global settings block
        if (topXSection.isConfigurationSection("rewardtopX-settings")) {
            copySection(
                topXSection.getConfigurationSection("rewardtopX-settings"),
                synth.createSection("rewardtopX-settings")
            );
        }

        // Everything else goes under <mobName> (positions, flags, etc.)
        ConfigurationSection mobDest = synth.createSection(mobName);
        for (String k : topXSection.getKeys(false)) {
            if ("topX".equalsIgnoreCase(k) || "rewardtopX-settings".equalsIgnoreCase(k)) continue;

            if (topXSection.isConfigurationSection(k)) {
                copySection(topXSection.getConfigurationSection(k), mobDest.createSection(k));
            } else {
                mobDest.set(k, topXSection.get(k));
            }
        }

        return Map.entry(topX, synth);
    }

    private static void copySection(ConfigurationSection src, ConfigurationSection dest) {
        if (src == null || dest == null) return;
        for (String k : src.getKeys(false)) {
            if (src.isConfigurationSection(k)) {
                copySection(src.getConfigurationSection(k), dest.createSection(k));
            } else {
                dest.set(k, src.get(k));
            }
        }
    }
}
