package fr.elias.mythicDrop.effects;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Map;

public class EffectInitializer {

    public static void registerDefaults() {
        EffectRegistry.register("FIREWORK", (location, data) -> {
            Firework firework = location.getWorld().spawn(location, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower((int) data.getOrDefault("power", 1));

            FireworkEffect.Builder effect = FireworkEffect.builder()
                    .withColor(Color.fromRGB(getColor((String) data.getOrDefault("color", "WHITE"))));

            if ((boolean) data.getOrDefault("flicker", false)) {
                effect.withFlicker();
            }

            meta.addEffect(effect.build());
            firework.setFireworkMeta(meta);

        });

        EffectRegistry.register("SOUND", (location, data) -> {
            String soundName = (String) data.get("sound");
            if (soundName != null) {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                float volume = ((Number) data.getOrDefault("volume", 1.0)).floatValue();
                float pitch = ((Number) data.getOrDefault("pitch", 1.0)).floatValue();

                location.getWorld().playSound(location, sound, volume, pitch);
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
}
