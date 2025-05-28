package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.MythicDrop.top5Config;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class OtherRewardsProcessor {

    public static void processEveryoneElseRewards(String mobName, Player player, double playerDamage) {
        MythicDrop plugin = MythicDrop.getInstance();
        // Ensure the player and their name are valid
        if (player == null) {
            logDebug("Player or player name is null. Skipping reward processing.");
            return;
        } else {
            player.getName();
        }

        logDebug("Processing rewards for everyone else who contributed for mob: " + mobName + ", player: " + player.getName());

        // Determine the appropriate configuration section (top5Config or top3Config)
        ConfigurationSection everyoneElseConfig = top5Config.getConfigurationSection(mobName + ".everyone-else-who-contributed");
        if (everyoneElseConfig == null) {
            everyoneElseConfig = top3Config.getConfigurationSection(mobName + ".everyone-else-who-contributed");
        }

        if (everyoneElseConfig == null) {
            logDebug("No valid 'everyone-else-who-contributed' section found for mob: " + mobName);
            return;
        }

        // Check for the minimum damage threshold
        double minDamage = everyoneElseConfig.getDouble("min-damage", 0.0); // Default to 0.0
        if (playerDamage < minDamage) {
            logDebug("Player " + player.getName() + " did not meet the min-damage threshold (" + minDamage + "). Skipping rewards.");
            return;
        }

        // Determine the player's primary group using LuckPerms
        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " primary group: " + primaryGroup);

        // Fetch group-specific drops or fallback to the default section
        ConfigurationSection groupDrops = everyoneElseConfig.getConfigurationSection(primaryGroup);
        ConfigurationSection effectiveGroupDrops = (groupDrops != null) ? groupDrops : everyoneElseConfig.getConfigurationSection("default");

        // Check if there are valid drops configured
        if (effectiveGroupDrops == null) {
            logDebug("No valid drop configuration found for player group: " + primaryGroup + " or default.");
            return;
        }

        // Process each drop in the configuration
        effectiveGroupDrops.getKeys(false).forEach(dropKey -> {
            double chance = effectiveGroupDrops.getDouble(dropKey + ".chance", 0.0); // Default to 0.0 chance if not configured
            double roll = ThreadLocalRandom.current().nextDouble(); // Thread-safe random number generation
            logDebug("Processing reward " + dropKey + " for player: " + player.getName() + " | Roll: " + roll + " | Chance: " + chance);

            // Check if the reward should trigger
            if (roll <= chance) {
                String command = effectiveGroupDrops.getString(dropKey + ".command");
                if (command == null || command.trim().isEmpty()) {
                    logDebug("Invalid or missing command for reward " + dropKey + " in configuration. Skipping.");
                    return;
                }

                // Execute the reward command
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                logDebug("Executed reward command: " + command.replace("%player%", player.getName()) + " | Success: " + success);

                // Send a message to the player if configured
                String message = effectiveGroupDrops.getString(dropKey + ".message");
                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent message to player: " + message);
                }
            } else {
                logDebug("Reward " + dropKey + " did not trigger. Chance threshold not met.");
            }
        });
    }
}
