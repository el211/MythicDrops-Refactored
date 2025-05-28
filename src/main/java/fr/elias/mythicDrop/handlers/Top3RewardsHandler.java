package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import fr.elias.mythicDrop.rewards.Top3Reward;
import fr.elias.mythicDrop.rewards.EveryoneElseReward;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import java.util.*;

import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.announcers.AnnounceDamageRanking.announceDamageRanking;
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

        logDebug("Executing Top 3 rewards for mob: " + mobName);

        List<String> rewardTop3Mobs = top3Config.getStringList("rewardtop3");
        if (!rewardTop3Mobs.contains(mobName)) {
            logDebug("Mob " + mobName + " is not configured for top-3 rewards. Skipping.");
            return;
        }

        if (!activeMob.hasThreatTable()) {
            logDebug("No Threat Table found for mob: " + mobName);
            return;
        }

        Set<AbstractEntity> damageRanking = activeMob.getThreatTable().getAllThreatTargets();
        if (damageRanking.isEmpty()) {
            logDebug("No players contributed damage to the mob: " + mobName);
            return;
        }

        // Sorting by highest damage first
        List<AbstractEntity> sortedRanking = new ArrayList<>(damageRanking);
        sortedRanking.sort(Comparator.comparingDouble(activeMob.getThreatTable()::getThreat).reversed());
        logDebug("Sorted ranking size: " + sortedRanking.size());

        if (!top3Config.contains(mobName)) {
            logDebug("No specific top-3 rewards configured for mob: " + mobName);
            return;
        }

        boolean useStandardRewards = top3Config.getBoolean(mobName + ".use-standard-rewards", false);
        logDebug("Use standard rewards for mob " + mobName + ": " + useStandardRewards);

        // Define rank names explicitly
        String[] ranks = {"first-place", "second-place", "third-place"};

        // Reward the top 3 players
        for (int i = 0; i < Math.min(3, sortedRanking.size()); i++) {
            AbstractEntity entity = sortedRanking.get(i);
            if (entity.isPlayer()) {
                Player player = (Player) entity.asPlayer().getBukkitEntity();
                String rank = ranks[i];

                logDebug("Rewarding player " + player.getName() + " for " + rank +
                        (useStandardRewards ? " (with standard rewards)" : ""));

                new RewardProcessor(new Top3Reward(rank, useStandardRewards)).execute(player, activeMob);

            } else {
                logDebug("Entity " + entity.getName() + " is not a player. Skipping.");
            }
        }

        // Reward everyone else
        double minDamage = top3Config.getDouble(mobName + ".everyone-else-who-contributed.min-damage", 0.0);
        logDebug("Minimum damage for 'everyone else' rewards: " + minDamage);

        int otherPlayersCount = 0;
        for (int i = 3; i < sortedRanking.size(); i++) {
            AbstractEntity entity = sortedRanking.get(i);
            if (entity.isPlayer()) {
                Player player = (Player) entity.asPlayer().getBukkitEntity();
                double playerDamage = activeMob.getThreatTable().getThreat(entity);

                if (playerDamage >= minDamage) {
                    logDebug("Rewarding player " + player.getName() + " for contributing with damage: " + playerDamage);
                    new RewardProcessor(new EveryoneElseReward(playerDamage)).execute(player, activeMob);
                    otherPlayersCount++;
                } else {
                    logDebug("Player " + player.getName() + " did not meet min-damage threshold (" + minDamage + "). Skipping.");
                }
            } else {
                logDebug("Entity " + entity.getName() + " is not a player. Skipping.");
            }
        }

        logDebug("Total 'everyone else' rewards given: " + otherPlayersCount);

        logDebug("Announcing damage ranking for mob: " + mobName);
        announceDamageRanking(activeMob);

        long duration = System.currentTimeMillis() - startTime;
        logDebug("handleTop3Rewards for mob " + mobName + " executed in " + duration + " ms.");
        plugin.getProcessedTop3Events().remove(mobId);
    }
}
