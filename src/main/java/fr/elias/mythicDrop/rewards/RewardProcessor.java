package fr.elias.mythicDrop.rewards;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class RewardProcessor {
    private Reward rewardStrategy;

    public RewardProcessor(Reward rewardStrategy) {
        this.rewardStrategy = rewardStrategy;
    }

    public void execute(Player player, ActiveMob mob) {
        if (player == null) {
            logDebug("Skipping reward execution: player is null.");
            return;
        }
        rewardStrategy.send(player, mob);
    }
}
