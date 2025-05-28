// File: src/main/java/fr/elias/mythicDrop/rewards/Top3Reward.java
package fr.elias.mythicDrop.rewards;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.processors.Top3RewardsProcessor.processTop3RewardsForPlayer;
import static fr.elias.mythicDrop.processors.StandardRewardProcessor.processStandardRewardsForPlayer;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class Top3Reward implements Reward {
    private final String rank;
    private final boolean useStandardRewards;

    public Top3Reward(String rank) {
        this(rank, false); // default: no standard rewards
    }

    public Top3Reward(String rank, boolean useStandardRewards) {
        this.rank = rank;
        this.useStandardRewards = useStandardRewards;
    }

    @Override
    public void send(Player player, ActiveMob mob) {
        String mobName = mob.getType().getInternalName();

        logDebug("Starting processing top-3 rewards for mob: " + mobName +
                ", rank: " + rank + ", player: " + player.getName());

        processTop3RewardsForPlayer(mobName, rank, player);

        if (useStandardRewards) {
            logDebug("Applying additional standard rewards for " + player.getName());
            processStandardRewardsForPlayer(player, mobName);
        }

        logDebug("Completed processing top-3 rewards for mob: " + mobName +
                ", rank: " + rank + ", player: " + player.getName());
    }
}
