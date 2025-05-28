package fr.elias.mythicDrop.utils;

import fr.elias.mythicDrop.MythicDrop;
import lombok.Getter;

import static fr.elias.mythicDrop.MythicDrop.debugConfig;

@Getter
public class DebugLogger {

    /**
     * Log debug messages if debug mode is enabled.
     * @param message The debug message to log.
     */
    public static void logDebug(String message) {
        MythicDrop plugin = MythicDrop.getInstance();
        try {
            // Ensure debugConfig is initialized before checking its values
            if (debugConfig != null && debugConfig.getBoolean("activate-debug", false)) {
                plugin.getLogger().info("[DEBUG] " + message);
            }
        } catch (Exception e) {
            // Log any issues with debug logging itself
            plugin.getLogger().severe("An error occurred while attempting to log a debug message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
