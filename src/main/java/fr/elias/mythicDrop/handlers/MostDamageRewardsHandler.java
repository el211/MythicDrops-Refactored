package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.rewards.MostDamageReward;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class MostDamageRewardsHandler {

    public static void handle(ActiveMob activeMob) {
        if (!activeMob.hasThreatTable()) {
            logDebug("No Threat Table found for mob: " + activeMob.getType().getInternalName());
            return;
        }

        AbstractEntity topThreatHolder = activeMob.getThreatTable().getTopThreatHolder();
        if (topThreatHolder == null || !topThreatHolder.isPlayer()) {
            logDebug("No player found with top threat for mob: " + activeMob.getType().getInternalName());
            return;
        }

        Player rewardPlayer = (Player) topThreatHolder.asPlayer().getBukkitEntity();
        logDebug("Most-damage player: " + rewardPlayer.getName());

        // Process reward using the Strategy pattern
        new RewardProcessor(new MostDamageReward()).execute(rewardPlayer, activeMob);
    }
}
