package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static fr.elias.mythicDrop.MythicDrop.top5Config;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class Top5RewardsProcessor {

    public static void processTop5RewardsForPlayer(String mobName, String rank, Player player) {
        MythicDrop plugin = MythicDrop.getInstance();
        long startTime = System.currentTimeMillis(); // Start timing for performance monitoring

        logDebug("Processing top-5 rewards for mob: " + mobName + ", rank: " + rank + ", player: " + player.getName());

        // Check if the rank is configured
        if (!top5Config.contains(mobName + "." + rank)) {
            logDebug("No reward configuration for rank: " + rank + " for mob: " + mobName);
            return;
        }

        // Fetch the rank-specific section
        ConfigurationSection rankSection = top5Config.getConfigurationSection(mobName + "." + rank);
        if (rankSection == null) {
            logDebug("No reward section found for " + mobName + " at rank: " + rank);
            return;
        }

        // Determine the player's primary group
        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " primary group: " + primaryGroup);

        // Fetch group-specific or default drops
        ConfigurationSection groupDrops = rankSection.contains(primaryGroup)
                ? rankSection.getConfigurationSection(primaryGroup)
                : rankSection.getConfigurationSection("default");
        if (groupDrops == null) {
            logDebug("No valid drop configuration found for player group: " + primaryGroup + " or default.");
            return;
        }

        // Log the keys in the group drops for better visibility
        logDebug("Reward keys available for player group " + primaryGroup + ": " + groupDrops.getKeys(false));

        // Get the guaranteed-rewards value, defaulting to 1 if not set
        int guaranteedRewards = rankSection.contains("guaranteed-rewards") ? rankSection.getInt("guaranteed-rewards") : 1;
        logDebug("DEBUG CHECK: guaranteed-rewards for " + mobName + " at rank " + rank + " = " + guaranteedRewards);
        logDebug("Guaranteed rewards for top-5 rank " + rank + ": " + guaranteedRewards);

        // Collect potential rewards
        List<String> rewardKeys = new ArrayList<>(groupDrops.getKeys(false));
        List<String> selectedRewards = new ArrayList<>();

        // Shuffle the rewards list to randomize the selection
        Collections.shuffle(rewardKeys);

        // Loop through the shuffled rewards and select up to guaranteedRewards
        for (String dropKey : rewardKeys) {
            if (selectedRewards.size() >= guaranteedRewards) break;

            double chance = groupDrops.getDouble(dropKey + ".chance", 0.0);
            String command = groupDrops.getString(dropKey + ".command");
            String message = groupDrops.getString(dropKey + ".message");

            logDebug("Processing reward " + dropKey + " | Chance: " + chance);

            if (command == null || command.isEmpty()) {
                logDebug("Invalid or missing command for reward: " + dropKey + ". Skipping.");
                continue;
            }

            // Roll the chance and apply the reward if successful
            double roll = ThreadLocalRandom.current().nextDouble();
            logDebug("Reward " + dropKey + ": Roll=" + roll + " | Threshold=" + chance);

            if (roll <= chance) {
                selectedRewards.add(dropKey);

                // Execute the command
                boolean commandSuccess = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                logDebug("Executed reward command for " + dropKey + ": " + command.replace("%player%", player.getName()) + ", Success: " + commandSuccess);

                // Send the reward message, if specified
                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent reward message to player: " + message);
                } else {
                    logDebug("No message specified for reward: " + dropKey + ".");
                }
            } else {
                logDebug("Reward " + dropKey + " did not trigger due to chance roll.");
            }
        }

        long duration = System.currentTimeMillis() - startTime; // Calculate duration
        logDebug("Finished processing top-5 rewards for mob: " + mobName + ", rank: " + rank + ", player: " + player.getName() + " in " + duration + " ms.");
    }
}
