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
import static fr.elias.mythicDrop.utils.ConfigLookup.getGroupSection;
import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.ConfigLookup.resolveKeyIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class Top5RewardsProcessor {

    public static void processTop5RewardsForPlayer(String mobName, String rank, Player player) {
        MythicDrop plugin = MythicDrop.getInstance();
        long startTime = System.currentTimeMillis();

        logDebug("Processing top-5 rewards for mob: " + mobName + ", rank: " + rank + ", player: " + player.getName());

        ConfigurationSection mobSection = getSectionIgnoreCase(top5Config, mobName);
        ConfigurationSection rankSection = mobSection == null ? null : getSectionIgnoreCase(mobSection, rank);
        if (rankSection == null || mobSection == null) {
            logDebug("No reward section found for mob: " + mobName + " at rank: " + rank);
            return;
        }

        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " primary group: " + primaryGroup);

        ConfigurationSection groupDrops = getGroupSection(rankSection, primaryGroup);
        if (groupDrops == null) {
            logDebug("No valid drop config found for group " + primaryGroup + " or default.");
            return;
        }

        boolean flexibleMode = top5Config.getBoolean("rewardtop5-settings.use-flexible-rewards", false);
        boolean guaranteedPerRank = mobSection.getBoolean("guaranteedperrank", false);
        boolean perRankGroup = mobSection.getBoolean("per-rank-group", false);

        int guaranteedRewards = resolveGuaranteedRewards(mobSection, rankSection, primaryGroup, guaranteedPerRank, perRankGroup);

        logDebug("Flexible mode: " + flexibleMode);
        logDebug("Guaranteed rewards: " + guaranteedRewards);

        List<String> rewardKeys = new ArrayList<>(groupDrops.getKeys(false));
        Collections.shuffle(rewardKeys);
        if (rewardKeys.size() < guaranteedRewards) {
            guaranteedRewards = rewardKeys.size();
        }

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

                if (!flexibleMode && guaranteedRewards > 0 && guaranteedGiven >= guaranteedRewards) {
                    break;
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logDebug("Finished processing top-5 rewards for mob: " + mobName + ", rank: " + rank +
                ", player: " + player.getName() + " in " + duration + " ms.");
    }

    private static int resolveGuaranteedRewards(ConfigurationSection mobSection,
                                                ConfigurationSection rankSection,
                                                String primaryGroup,
                                                boolean guaranteedPerRank,
                                                boolean perRankGroup) {
        int guaranteedRewards = resolveGuaranteedValue(mobSection, perRankGroup ? primaryGroup : null, 1);
        if (guaranteedPerRank) {
            guaranteedRewards = resolveGuaranteedValue(rankSection, perRankGroup ? primaryGroup : null, guaranteedRewards);
        } else if (perRankGroup) {
            guaranteedRewards = resolveGuaranteedValue(mobSection, primaryGroup, guaranteedRewards);
        }
        return Math.max(0, guaranteedRewards);
    }

    private static int resolveGuaranteedValue(ConfigurationSection section, String primaryGroup, int fallback) {
        if (section == null || !section.contains("guaranteed-rewards")) {
            return fallback;
        }

        if (section.isConfigurationSection("guaranteed-rewards")) {
            ConfigurationSection guaranteedRewardsSection = section.getConfigurationSection("guaranteed-rewards");
            if (guaranteedRewardsSection == null) {
                return fallback;
            }

            if (primaryGroup != null) {
                String resolvedGroupKey = resolveKeyIgnoreCase(guaranteedRewardsSection, primaryGroup);
                if (resolvedGroupKey != null) {
                    return guaranteedRewardsSection.getInt(resolvedGroupKey, fallback);
                }
            }

            return guaranteedRewardsSection.getInt("default", fallback);
        }

        return section.getInt("guaranteed-rewards", fallback);
    }
}
