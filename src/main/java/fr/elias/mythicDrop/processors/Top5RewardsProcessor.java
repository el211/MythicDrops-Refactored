package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static fr.elias.mythicDrop.MythicDrop.top5Config;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class Top5RewardsProcessor {

    public static void processTop5RewardsForPlayer(String mobName, String rank, Player player) {
        MythicDrop plugin = MythicDrop.getInstance();
        long startTime = System.currentTimeMillis();

        logDebug("Processing top-5 rewards for mob: " + mobName + ", rank: " + rank + ", player: " + player.getName());

        // Check if reward section for this rank exists
        ConfigurationSection rankSection = top5Config.getConfigurationSection(mobName + "." + rank);
        if (rankSection == null) {
            logDebug("No reward section found for mob: " + mobName + " at rank: " + rank);
            return;
        }

        // Get primary group (e.g., VIP, default)
        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " primary group: " + primaryGroup);

        // Try group section or default
        ConfigurationSection groupDrops = rankSection.contains(primaryGroup)
                ? rankSection.getConfigurationSection(primaryGroup)
                : rankSection.getConfigurationSection("default");

        if (groupDrops == null) {
            logDebug("No valid drop config found for group " + primaryGroup + " or default.");
            return;
        }

        // Determine reward mode
        boolean flexibleMode = top5Config.getBoolean("rewardtop5-settings.use-flexible-rewards", false);
        boolean guaranteedPerRank = top5Config.getBoolean(mobName + ".guaranteedperrank", false);
        boolean perRankGroup = top5Config.getBoolean(mobName + ".per-rank-group", false);

        // Default fallback
        int guaranteedRewards = top5Config.getInt(mobName + ".guaranteed-rewards.default", 1);

        // Check per-group override
        if (perRankGroup) {
            String groupKey = "guaranteed-rewards." + primaryGroup;
            guaranteedRewards = top5Config.getInt(mobName + "." + groupKey, guaranteedRewards);
        } else if (guaranteedPerRank) {
            guaranteedRewards = rankSection.getInt("guaranteed-rewards", guaranteedRewards);
        }

        logDebug("Flexible mode: " + flexibleMode);
        logDebug("Guaranteed rewards: " + guaranteedRewards);

        List<String> rewardKeys = new ArrayList<>(groupDrops.getKeys(false));
        Collections.shuffle(rewardKeys);  // Randomize reward order

        int guaranteedGiven = 0;

        for (String dropKey : rewardKeys) {
            String command = groupDrops.getString(dropKey + ".command");
            String message = groupDrops.getString(dropKey + ".message");
            double chance = groupDrops.getDouble(dropKey + ".chance", 0.0);
            double roll = ThreadLocalRandom.current().nextDouble();

            boolean isGuaranteed = guaranteedGiven < guaranteedRewards;

            logDebug("Evaluating reward: " + dropKey + " | Guaranteed: " + isGuaranteed + " | Chance: " + chance + " | Roll: " + roll);

            if (command == null || command.isEmpty()) {
                logDebug("Invalid or missing command for reward: " + dropKey + ". Skipping.");
                continue;
            }

            boolean shouldGive = isGuaranteed || roll <= chance;

            if (shouldGive) {
                boolean success = Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())
                );
                logDebug("Executed command for " + dropKey + ": " + command + " | Success: " + success);

                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent message to player: " + message);
                }

                if (isGuaranteed) {
                    guaranteedGiven++;
                }

                // Legacy mode: stop once we've given enough guaranteed rewards
                if (!flexibleMode && guaranteedGiven >= guaranteedRewards) {
                    break;
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logDebug("Finished processing top-5 rewards for mob: " + mobName + ", rank: " + rank +
                ", player: " + player.getName() + " in " + duration + " ms.");
    }
}
