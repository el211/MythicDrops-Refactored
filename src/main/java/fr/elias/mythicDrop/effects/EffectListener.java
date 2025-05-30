package fr.elias.mythicDrop.effects;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.effects.EffectRegistry;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class EffectListener implements Listener {

    @EventHandler
    public void onMobDeath(MythicMobDeathEvent event) {
        ActiveMob activeMob = MythicBukkit.inst().getMobManager()
                .getActiveMob(event.getEntity().getUniqueId())
                .orElse(null);
        if (activeMob == null) return;

        String mobName = activeMob.getType().getInternalName();
        ConfigurationSection mobSection = MythicDrop.effectsConfig.getConfigurationSection(mobName + ".effects");
        if (mobSection == null) return;

        for (String effectKey : mobSection.getKeys(false)) {
            ConfigurationSection effectData = mobSection.getConfigurationSection(effectKey);
            if (effectData == null) continue;

            String type = effectData.getString("type");
            if (type == null || !EffectRegistry.has(type)) continue;

            EffectRegistry.get(type).execute(event.getEntity().getLocation(), (Map) effectData.getValues(true));
        }
    }
}

