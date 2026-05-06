package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.rewards.MostDamageReward;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import fr.elias.mythicDrop.utils.DamageTracker;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import java.util.List;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class MostDamageRewardsHandler {

    public static void handle(ActiveMob activeMob) {
        List<DamageTracker.DamageEntry> sortedRanking = DamageTracker.getSortedDamageRanking(activeMob.getUniqueId());
        if (sortedRanking.isEmpty()) {
            logDebug("No tracked player damage found for mob: " + activeMob.getType().getInternalName());
            return;
        }

        DamageTracker.DamageEntry topDamageEntry = sortedRanking.get(0);
        Player rewardPlayer = DamageTracker.getOnlinePlayer(topDamageEntry);
        if (rewardPlayer == null) {
            logDebug("Top-damage player is no longer online for mob: " + activeMob.getType().getInternalName());
            return;
        }

        logDebug("Most-damage player: " + topDamageEntry.getPlayerName() + " with " + topDamageEntry.getDamage() + " damage.");
        new RewardProcessor(new MostDamageReward()).execute(rewardPlayer, activeMob);
    }
}
