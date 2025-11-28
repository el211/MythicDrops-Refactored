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

import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class Top3RewardsProcessor {

    public static void processTop3RewardsForPlayer(String mobName, String rank, Player player) {
        MythicDrop plugin = MythicDrop.getInstance();
        long startTime = System.currentTimeMillis();

        logDebug("Starting processing top-3 rewards for mob: " + mobName + ", rank: " + rank + ", player: " + player.getName());

        if (!top3Config.contains(mobName + "." + rank)) {
            logDebug("No rewards configured for " + rank + " of mob: " + mobName);
            return;
        }

        ConfigurationSection mobSection = top3Config.getConfigurationSection(mobName);
        ConfigurationSection rankSection = top3Config.getConfigurationSection(mobName + "." + rank);
        if (rankSection == null || mobSection == null) {
            logDebug("Missing configuration section for " + mobName + " or rank " + rank);
            return;
        }

        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " belongs to group: " + primaryGroup);

        ConfigurationSection groupDrops = rankSection.contains(primaryGroup)
                ? rankSection.getConfigurationSection(primaryGroup)
                : rankSection.getConfigurationSection("default");

        if (groupDrops == null) {
            logDebug("No drop config found for group " + primaryGroup + " or 'default'");
            return;
        }

        // NEW: Global toggle for flexible mode
        boolean useFlexible = top3Config.getBoolean("rewardtop3-settings.use-flexible-rewards", false);
        logDebug("Flexible reward mode: " + useFlexible);

        boolean guaranteedPerRank = mobSection.getBoolean("guaranteedperrank", false);
        boolean perRankGroup = mobSection.getBoolean("per-rank-group", false);

        int guaranteedRewards = mobSection.getInt("guaranteed-rewards.default", 1);
        if (perRankGroup) {
            guaranteedRewards = mobSection.getInt("guaranteed-rewards." + primaryGroup, guaranteedRewards);
        } else if (guaranteedPerRank) {
            guaranteedRewards = rankSection.getInt("guaranteed-rewards", guaranteedRewards);
        }

        logDebug("Final guaranteedRewards = " + guaranteedRewards);

        List<String> rewardKeys = new ArrayList<>(groupDrops.getKeys(false));
        Collections.shuffle(rewardKeys);
        int given = 0;

        for (int i = 0; i < rewardKeys.size(); i++) {
            String dropKey = rewardKeys.get(i);
            String command = groupDrops.getString(dropKey + ".command");
            String message = groupDrops.getString(dropKey + ".message");
            double chance = groupDrops.getDouble(dropKey + ".chance", 0.0);
            double roll = ThreadLocalRandom.current().nextDouble();

            boolean isGuaranteed = i < guaranteedRewards;

            logDebug("Reward " + dropKey + " | Guaranteed: " + isGuaranteed + " | Chance: " + chance + " | Roll: " + roll);

            if (command == null || command.isEmpty()) {
                logDebug("Skipping invalid drop: " + dropKey);
                continue;
            }

            boolean give = isGuaranteed || roll <= chance;
            if (give) {
                boolean success = Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())
                );
                logDebug("Executed command for " + dropKey + " | Success: " + success);

                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }

                given++;

                // 🚨 OLD mode: break after hitting guaranteed number
                if (!useFlexible && given >= guaranteedRewards) {
                    logDebug("Reached guaranteed rewards limit. Breaking loop.");
                    break;
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logDebug("Finished Top-3 rewards for " + mobName + " rank " + rank + " in " + duration + " ms.");
    }
}
