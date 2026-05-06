package fr.elias.mythicDrop.effects;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Map;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class EffectInitializer {

    public static void registerDefaults() {
        EffectRegistry.register("FIREWORK", (location, data) -> {
            if (location == null || location.getWorld() == null) {
                logDebug("Skipping firework effect because the location or world is null.");
                return;
            }

            Firework firework = location.getWorld().spawn(location, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(getInt(data.get("power"), 1));

            FireworkEffect.Builder effect = FireworkEffect.builder()
                    .withColor(Color.fromRGB(getColor((String) data.getOrDefault("color", "WHITE"))));

            if (getBoolean(data.get("flicker"), false)) {
                effect.withFlicker();
            }
            if (getBoolean(data.get("trail"), false)) {
                effect.withTrail();
            }

            meta.addEffect(effect.build());
            firework.setFireworkMeta(meta);
        });

        EffectRegistry.register("SOUND", (location, data) -> {
            if (location == null || location.getWorld() == null) {
                logDebug("Skipping sound effect because the location or world is null.");
                return;
            }

            String soundName = (String) data.get("sound");
            if (soundName != null) {
                try {
                    Sound sound = Sound.valueOf(soundName.toUpperCase());
                    float volume = getFloat(data.get("volume"), 1.0f);
                    float pitch = getFloat(data.get("pitch"), 1.0f);

                    location.getWorld().playSound(location, sound, volume, pitch);
                } catch (IllegalArgumentException ex) {
                    logDebug("Invalid sound configured in effects.yml: " + soundName);
                }
            }
        });
    }

    private static int getColor(String name) {
        switch (name.toUpperCase()) {
            case "RED": return Color.RED.asRGB();
            case "BLUE": return Color.BLUE.asRGB();
            case "GREEN": return Color.GREEN.asRGB();
            case "YELLOW": return Color.YELLOW.asRGB();
            case "WHITE": return Color.WHITE.asRGB();
            default: return Color.WHITE.asRGB();
        }
    }

    private static int getInt(Object value, int fallback) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return value == null ? fallback : Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static float getFloat(Object value, float fallback) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }

        try {
            return value == null ? fallback : Float.parseFloat(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static boolean getBoolean(Object value, boolean fallback) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value == null) {
            return fallback;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
