package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.rewards.EveryoneElseReward;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import fr.elias.mythicDrop.rewards.TopXReward;
import fr.elias.mythicDrop.utils.DamageTracker;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.ConfigLookup.listContainsIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class TopXRewardsHandler {

    private static final Set<UUID> processing = new HashSet<>();

    public static void handle(ActiveMob activeMob, int topX, YamlConfiguration config) {
        long startTime = System.currentTimeMillis();
        String mobName = activeMob.getType().getInternalName();
        UUID mobId = activeMob.getUniqueId();

        if (processing.contains(mobId)) {
            logDebug("TopX rewards for mob " + mobName + " are already being processed. Skipping.");
            return;
        }
        processing.add(mobId);

        try {
            logDebug("Executing TopX (" + topX + ") rewards for mob: " + mobName);

            if (!listContainsIgnoreCase(config.getStringList("rewardtopX"), mobName)) {
                logDebug("Mob " + mobName + " is not configured for topX rewards. Skipping.");
                return;
            }

            List<DamageTracker.DamageEntry> sortedRanking = DamageTracker.getSortedDamageRanking(activeMob.getUniqueId());
            if (sortedRanking.isEmpty()) {
                logDebug("No players contributed damage to the mob: " + mobName);
                return;
            }

            logDebug("Sorted ranking size: " + sortedRanking.size());

            ConfigurationSection mobSection = getSectionIgnoreCase(config, mobName);
            if (mobSection == null) {
                logDebug("No specific topX rewards configured for mob: " + mobName);
                return;
            }

            boolean useStandardRewards = mobSection.getBoolean("use-standard-rewards", false);
            logDebug("Use standard rewards for mob " + mobName + ": " + useStandardRewards);

            for (int i = 0; i < Math.min(topX, sortedRanking.size()); i++) {
                DamageTracker.DamageEntry damageEntry = sortedRanking.get(i);
                Player player = DamageTracker.getOnlinePlayer(damageEntry);
                if (player == null) {
                    logDebug("Player at rank " + (i + 1) + " is no longer online for mob " + mobName + ".");
                    continue;
                }

                String positionKey = "position-" + (i + 1);
                logDebug("Rewarding player " + player.getName() + " for " + positionKey +
                        (useStandardRewards ? " (with standard rewards)" : ""));

                new RewardProcessor(new TopXReward(config, "rewardtopX-settings", positionKey, useStandardRewards))
                        .execute(player, activeMob);
            }

            // Everyone-else-who-contributed section
            ConfigurationSection everyoneElseSection = mobSection.getConfigurationSection("everyone-else-who-contributed");
            double minDamage = everyoneElseSection == null ? 0.0 : everyoneElseSection.getDouble("min-damage", 0.0);
            logDebug("Minimum damage for 'everyone else' rewards: " + minDamage);

            int otherPlayersCount = 0;
            for (int i = topX; i < sortedRanking.size(); i++) {
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
            logDebug("handleTopXRewards for mob " + mobName + " (topX=" + topX + ") executed in " + duration + " ms.");
        } finally {
            processing.remove(mobId);
        }
    }
}
