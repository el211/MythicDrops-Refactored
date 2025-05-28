// File: src/main/java/fr/elias/mythicDrop/rewards/Top5Reward.java
package fr.elias.mythicDrop.rewards;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.processors.Top5RewardsProcessor.processTop5RewardsForPlayer;
import static fr.elias.mythicDrop.processors.StandardRewardProcessor.processStandardRewardsForPlayer;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class Top5Reward implements Reward {
    private final String rank;
    private final boolean useStandardRewards;

    public Top5Reward(String rank) {
        this(rank, false); // default: no standard rewards
    }

    public Top5Reward(String rank, boolean useStandardRewards) {
        this.rank = rank;
        this.useStandardRewards = useStandardRewards;
    }

    @Override
    public void send(Player player, ActiveMob mob) {
        String mobName = mob.getType().getInternalName();

        logDebug("Starting processing top-5 rewards for mob: " + mobName +
                ", rank: " + rank + ", player: " + player.getName());

        processTop5RewardsForPlayer(mobName, rank, player);

        if (useStandardRewards) {
            logDebug("Applying additional standard rewards for " + player.getName() + " (Top 5).");
            processStandardRewardsForPlayer(player, mobName);
        }

        logDebug("Completed processing top-5 rewards for mob: " + mobName +
                ", rank: " + rank + ", player: " + player.getName());
    }
}
