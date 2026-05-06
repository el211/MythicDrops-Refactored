package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.rewards.LastHitReward;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class LastHitRewardsHandler {

    public static void handle(ActiveMob mob, Player lastHitter) {
        if (lastHitter != null) {
            logDebug("Assigning last-hit reward to " + lastHitter.getName());
            new RewardProcessor(new LastHitReward()).execute(lastHitter, mob);
        } else {
            logDebug("No valid last-hitter found.");
        }
    }
}
