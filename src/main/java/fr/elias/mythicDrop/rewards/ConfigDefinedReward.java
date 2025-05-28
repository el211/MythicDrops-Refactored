package fr.elias.mythicDrop.rewards;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class ConfigDefinedReward implements Reward {
    private final FileConfiguration config; // Use existing config.yml
    private final String mobName;

    public ConfigDefinedReward(String mobName) {
        this.config = MythicDrop.getInstance().getConfig(); // Load from main config.yml
        this.mobName = mobName;
        ensureMobConfigExists(); // Ensure the section exists safely
    }

    @Override
    public void send(Player player, ActiveMob mob) {
        if (player == null) {
            logDebug("No valid player found for config-based reward.");
            return;
        }

        // Fetch drop configuration for this mob
        ConfigurationSection mobConfig = config.getConfigurationSection(mobName + ".drops");
        if (mobConfig == null) {
            logDebug("No drop configuration found for mob: " + mobName);
            return;
        }

        // Fetch LuckPerms group dynamically
        String primaryGroup = MythicDrop.getInstance().getPrimaryGroup(player);
        if (primaryGroup == null || primaryGroup.isEmpty()) {
            logDebug("Player " + player.getName() + " has no valid LuckPerms group. Using 'default'.");
            primaryGroup = "default";
        } else {
            logDebug("Player " + player.getName() + " is in LuckPerms group: " + primaryGroup);
        }

        // Fetch group-specific rewards, fallback to "default" if not found
        ConfigurationSection groupDrops = mobConfig.getConfigurationSection(primaryGroup);
        if (groupDrops == null) {
            logDebug("No drops found for LuckPerms group '" + primaryGroup + "'. Using 'default' rewards.");
            groupDrops = mobConfig.getConfigurationSection("default");
        }

        if (groupDrops == null) {
            logDebug("No 'default' rewards found for mob: " + mobName);
            return;
        }

        // Process each drop in the configuration
        for (String dropKey : groupDrops.getKeys(false)) {
            double chance = groupDrops.getDouble(dropKey + ".chance", 0.0);
            String command = groupDrops.getString(dropKey + ".command");
            String message = groupDrops.getString(dropKey + ".message");

            if (command == null || command.trim().isEmpty()) {
                logDebug("Invalid or missing command for reward: " + dropKey + ". Skipping.");
                continue;
            }

            double roll = Math.random();
            logDebug("Processing reward " + dropKey + " for player " + player.getName() + " | Roll: " + roll + " | Chance: " + chance);

            if (roll <= chance) {
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                logDebug("Executed command: " + command + " | Success: " + success);

                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent reward message to player: " + message);
                }
            } else {
                logDebug("Reward " + dropKey + " did not trigger (failed chance roll).");
            }
        }
    }

    private void ensureMobConfigExists() {
        if (!config.contains(mobName + ".drops")) {
            logDebug("No drops found for " + mobName + ". Skipping default generation.");
        }
    }
}
