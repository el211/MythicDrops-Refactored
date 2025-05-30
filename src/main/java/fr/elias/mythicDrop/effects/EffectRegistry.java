package fr.elias.mythicDrop.effects;

import java.util.HashMap;
import java.util.Map;

public class EffectRegistry {
    private static final Map<String, EffectExecutor> handlers = new HashMap<>();

    public static void register(String type, EffectExecutor executor) {
        handlers.put(type.toUpperCase(), executor);
    }

    public static EffectExecutor get(String type) {
        return handlers.get(type.toUpperCase());
    }

    public static boolean has(String type) {
        return handlers.containsKey(type.toUpperCase());
    }

    public static void clear() {
        handlers.clear();
    }
}
