package fr.elias.mythicDrop.rewards;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.processors.PlayerRewardsProcessor.processRewardsForPlayer;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class LastHitReward implements Reward { // Change extends to implements
    @Override
    public void send(Player player, ActiveMob mob) {
        logDebug("Processing last-hit reward for " + player.getName());
        processRewardsForPlayer(mob, player, 1);
    }
}
