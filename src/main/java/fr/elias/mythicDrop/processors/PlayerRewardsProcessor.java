package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class PlayerRewardsProcessor {

    public static void processRewardsForPlayer(ActiveMob activeMob, Player player, int position) {
        MythicDrop plugin = MythicDrop.getInstance();
        FileConfiguration config = plugin.getConfig();

        long startTime = System.currentTimeMillis();

        String mobName = activeMob.getType().getInternalName();
        logDebug("Starting reward processing for mob: " + mobName + ", player: " + player.getName() + ", position: " + position);

        // Check if the mob has drop configuration in config.yml
        if (!config.contains(mobName + ".drops")) {
            logDebug("No drop configuration found for mob: " + mobName + " in config.yml.");
            return;
        }

        ConfigurationSection mobDrops = config.getConfigurationSection(mobName + ".drops");
        if (mobDrops == null) {
            logDebug("Failed to retrieve drops section for mob: " + mobName);
            return;
        }

        // Get the player's primary group for group-specific drops
        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " belongs to primary group: " + primaryGroup);

        ConfigurationSection groupDrops = mobDrops.contains(primaryGroup)
                ? mobDrops.getConfigurationSection(primaryGroup)
                : mobDrops.getConfigurationSection("default");

        if (groupDrops == null) {
            logDebug("No valid drop configuration found for player group: " + primaryGroup + " or default.");
            return;
        }

        logDebug("Processing rewards for player group: " + primaryGroup + " (or default). Available reward keys: " + groupDrops.getKeys(false));

        // Get the guaranteed-rewards value, defaulting to 1 if not set
        int guaranteedRewards = mobDrops.contains("guaranteed-rewards")
                ? mobDrops.getInt("guaranteed-rewards")
                : 0;  // <- Don't guarantee if not explicitly configured

        // Collect all potential reward keys
        List<String> rewardKeys = new ArrayList<>(groupDrops.getKeys(false));

        // Ensure we don't exceed the number of available reward keys
        if (rewardKeys.size() < guaranteedRewards) {
            logDebug("Not enough reward keys available for guaranteed-rewards: " + guaranteedRewards);
            guaranteedRewards = rewardKeys.size();
        }

        // Randomly shuffle the list of reward keys
        Collections.shuffle(rewardKeys);

        // Guarantee rewards without chance rolls
        logDebug("Guaranteeing " + guaranteedRewards + " rewards for player: " + player.getName());
        for (int i = 0; i < guaranteedRewards; i++) {
            String dropKey = rewardKeys.get(i);
            String command = groupDrops.getString(dropKey + ".command");
            String message = groupDrops.getString(dropKey + ".message");

            if (command != null && !command.isEmpty()) {
                // Execute the guaranteed reward
                boolean commandSuccess = Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())
                );
                logDebug("Executed guaranteed reward command for " + dropKey + ": " + command + ", Success: " + commandSuccess);

                // Send reward message if specified
                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent reward message to player: " + message);
                }
            } else {
                logDebug("Invalid or missing command for guaranteed reward: " + dropKey + ". Skipping.");
            }
        }

        // Process remaining rewards based on chance
        logDebug("Processing chance-based rewards for player: " + player.getName());
        for (String dropKey : rewardKeys.subList(guaranteedRewards, rewardKeys.size())) {
            double chance = groupDrops.getDouble(dropKey + ".chance", 0.0); // Default chance is 0
            String command = groupDrops.getString(dropKey + ".command");
            String message = groupDrops.getString(dropKey + ".message");

            logDebug("Processing chance-based reward " + dropKey + " | Chance: " + chance);

            if (command == null || command.trim().isEmpty()) {
                logDebug("Invalid or missing command for reward: " + dropKey + ". Skipping.");
                continue;
            }

            // Roll the chance for this reward
            double roll = ThreadLocalRandom.current().nextDouble();
            logDebug("Reward " + dropKey + ": Roll=" + roll + " | Threshold=" + chance);

            if (roll <= chance) {
                // Execute the reward command
                boolean commandSuccess = Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())
                );
                logDebug("Executed chance-based reward command for " + dropKey + ": " + command.replace("%player%", player.getName()) + ", Success: " + commandSuccess);

                // Send reward message to the player if configured
                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent reward message to player: " + message);
                }
            } else {
                logDebug("Reward " + dropKey + " did not trigger due to chance roll.");
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logDebug("Finished reward processing for mob: " + mobName + ", player: " + player.getName() + ", position: " + position + " in " + duration + " ms.");
    }
}
