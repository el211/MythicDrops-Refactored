package fr.elias.mythicDrop.utils;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Locale;

public final class ConfigLookup {

    private ConfigLookup() {
    }

    public static boolean listContainsIgnoreCase(List<String> values, String target) {
        if (values == null || target == null) {
            return false;
        }

        for (String value : values) {
            if (value != null && value.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    public static String resolveKeyIgnoreCase(ConfigurationSection section, String key) {
        if (section == null || key == null) {
            return null;
        }

        if (section.contains(key)) {
            return key;
        }

        String normalizedKey = key.toLowerCase(Locale.ROOT);
        for (String candidate : section.getKeys(false)) {
            if (candidate.toLowerCase(Locale.ROOT).equals(normalizedKey)) {
                return candidate;
            }
        }
        return null;
    }

    public static ConfigurationSection getSectionIgnoreCase(ConfigurationSection section, String key) {
        String resolvedKey = resolveKeyIgnoreCase(section, key);
        return resolvedKey == null ? null : section.getConfigurationSection(resolvedKey);
    }

    public static ConfigurationSection getGroupSection(ConfigurationSection section, String groupName) {
        if (section == null) {
            return null;
        }

        ConfigurationSection groupSection = getSectionIgnoreCase(section, groupName);
        if (groupSection != null) {
            return groupSection;
        }

        return getSectionIgnoreCase(section, "default");
    }
}
