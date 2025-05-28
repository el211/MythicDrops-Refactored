// File: src/main/java/fr/elias/mythicDrop/processors/StandardRewardProcessor.java
package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class StandardRewardProcessor {

    public static void processStandardRewardsForPlayer(Player player, String mobName) {
        FileConfiguration config = MythicDrop.getInstance().getConfig();
        ConfigurationSection standardRewards = config.getConfigurationSection(mobName + ".standard-rewards");

        if (standardRewards == null) {
            logDebug("No standard rewards defined for mob: " + mobName);
            return;
        }

        for (String dropKey : standardRewards.getKeys(false)) {
            String path = mobName + ".standard-rewards." + dropKey;

            String command = config.getString(path + ".command");
            String message = config.getString(path + ".message");
            double chance = config.getDouble(path + ".chance", 0.0);

            if (command == null || command.trim().isEmpty()) {
                logDebug("Skipping drop " + dropKey + ": Missing command.");
                continue;
            }

            double roll = Math.random();
            logDebug("Evaluating standard drop: " + dropKey + " | Roll=" + roll + " | Chance=" + chance);

            if (roll <= chance) {
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                logDebug("Executed standard drop command: " + command + " | Success: " + success);

                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent standard drop message: " + message);
                }
            } else {
                logDebug("Drop " + dropKey + " did not pass chance check.");
            }
        }
    }
}
