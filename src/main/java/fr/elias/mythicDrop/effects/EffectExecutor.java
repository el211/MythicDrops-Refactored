package fr.elias.mythicDrop.effects;


import org.bukkit.Location;

import java.util.Map;

@FunctionalInterface
public interface EffectExecutor {
    void execute(Location location, Map<String, Object> data);
}
