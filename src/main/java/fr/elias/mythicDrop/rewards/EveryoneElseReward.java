package fr.elias.mythicDrop.rewards;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.processors.OtherRewardsProcessor.processEveryoneElseRewards;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class EveryoneElseReward implements Reward { // Use implements, not extends
    private final double playerDamage;

    public EveryoneElseReward(double playerDamage) {
        this.playerDamage = playerDamage;
    }

    @Override
    public void send(Player player, ActiveMob mob) {
        logDebug("Processing everyone-else reward for " + player.getName());
        processEveryoneElseRewards(mob.getType().getInternalName(), player, playerDamage);
    }
}
