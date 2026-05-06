package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.rewards.EveryoneElseReward;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import fr.elias.mythicDrop.rewards.Top3Reward;
import fr.elias.mythicDrop.utils.DamageTracker;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.ConfigLookup.listContainsIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class Top3RewardsHandler {

    public static void handle(ActiveMob activeMob) {
        MythicDrop plugin = MythicDrop.getInstance();
        long startTime = System.currentTimeMillis();
        String mobName = activeMob.getType().getInternalName();

        UUID mobId = activeMob.getUniqueId();
        if (plugin.getProcessedTop3Events().contains(mobId)) {
            logDebug("Top 3 rewards for mob " + mobName + " have already been processed. Skipping.");
            return;
        }
        plugin.getProcessedTop3Events().add(mobId);

        try {
            logDebug("Executing Top 3 rewards for mob: " + mobName);

            if (!listContainsIgnoreCase(top3Config.getStringList("rewardtop3"), mobName)) {
                logDebug("Mob " + mobName + " is not configured for top-3 rewards. Skipping.");
                return;
            }

            List<DamageTracker.DamageEntry> sortedRanking = DamageTracker.getSortedDamageRanking(activeMob.getUniqueId());
            if (sortedRanking.isEmpty()) {
                logDebug("No players contributed damage to the mob: " + mobName);
                return;
            }

            logDebug("Sorted ranking size: " + sortedRanking.size());

            ConfigurationSection mobSection = getSectionIgnoreCase(top3Config, mobName);
            if (mobSection == null) {
                logDebug("No specific top-3 rewards configured for mob: " + mobName);
                return;
            }

            boolean useStandardRewards = mobSection.getBoolean("use-standard-rewards", false);
            logDebug("Use standard rewards for mob " + mobName + ": " + useStandardRewards);

            String[] ranks = {"first-place", "second-place", "third-place"};

            for (int i = 0; i < Math.min(3, sortedRanking.size()); i++) {
                DamageTracker.DamageEntry damageEntry = sortedRanking.get(i);
                Player player = DamageTracker.getOnlinePlayer(damageEntry);
                if (player == null) {
                    logDebug("Player at rank " + (i + 1) + " is no longer online for mob " + mobName + ".");
                    continue;
                }

                String rank = ranks[i];
                logDebug("Rewarding player " + player.getName() + " for " + rank +
                        (useStandardRewards ? " (with standard rewards)" : ""));

                new RewardProcessor(new Top3Reward(rank, useStandardRewards)).execute(player, activeMob);
            }

            ConfigurationSection everyoneElseSection = mobSection.getConfigurationSection("everyone-else-who-contributed");
            double minDamage = everyoneElseSection == null ? 0.0 : everyoneElseSection.getDouble("min-damage", 0.0);
            logDebug("Minimum damage for 'everyone else' rewards: " + minDamage);

            int otherPlayersCount = 0;
            for (int i = 3; i < sortedRanking.size(); i++) {
                DamageTracker.DamageEntry damageEntry = sortedRanking.get(i);
                Player player = DamageTracker.getOnlinePlayer(damageEntry);
                if (player == null) {
                    continue;
                }

                double playerDamage = damageEntry.getDamage();
                if (playerDamage >= minDamage) {
                    logDebug("Rewarding player " + player.getName() + " for contributing with damage: " + playerDamage);
                    new RewardProcessor(new EveryoneElseReward(playerDamage)).execute(player, activeMob);
                    otherPlayersCount++;
                } else {
                    logDebug("Player " + player.getName() + " did not meet min-damage threshold (" + minDamage + "). Skipping.");
                }
            }

            logDebug("Total 'everyone else' rewards given: " + otherPlayersCount);

            long duration = System.currentTimeMillis() - startTime;
            logDebug("handleTop3Rewards for mob " + mobName + " executed in " + duration + " ms.");
        } finally {
            plugin.getProcessedTop3Events().remove(mobId);
        }
    }
}
