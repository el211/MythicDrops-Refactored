package fr.elias.mythicDrop.effects;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public final class EffectListener {

    private EffectListener() {
    }

    public static void playConfiguredEffects(ActiveMob activeMob, Location location) {
        if (activeMob == null || location == null || location.getWorld() == null) {
            return;
        }

        String mobName = activeMob.getType().getInternalName();
        ConfigurationSection mobRoot = getSectionIgnoreCase(MythicDrop.effectsConfig, mobName);
        if (mobRoot == null) {
            return;
        }

        ConfigurationSection mobSection = mobRoot.getConfigurationSection("effects");
        if (mobSection == null) {
            return;
        }

        for (String effectKey : mobSection.getKeys(false)) {
            ConfigurationSection effectData = mobSection.getConfigurationSection(effectKey);
            if (effectData == null) {
                continue;
            }

            String type = effectData.getString("type");
            if (type == null || !EffectRegistry.has(type)) {
                logDebug("Skipping unknown effect type '" + type + "' for mob " + mobName + ".");
                continue;
            }

            try {
                EffectRegistry.get(type).execute(location, new HashMap<>(effectData.getValues(false)));
            } catch (Exception ex) {
                logDebug("Failed to execute effect '" + effectKey + "' for mob " + mobName + ": " + ex.getMessage());
            }
        }
    }
}
