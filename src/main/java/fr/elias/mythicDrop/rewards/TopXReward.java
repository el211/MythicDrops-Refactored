package fr.elias.mythicDrop.rewards;

import fr.elias.mythicDrop.processors.StandardRewardProcessor;
import fr.elias.mythicDrop.processors.TopXRewardsProcessor;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class TopXReward implements Reward {

    private final YamlConfiguration config;
    private final String settingsKey;
    private final String positionKey;
    private final boolean useStandardRewards;

    public TopXReward(YamlConfiguration config, String settingsKey, String positionKey, boolean useStandardRewards) {
        this.config = config;
        this.settingsKey = settingsKey;
        this.positionKey = positionKey;
        this.useStandardRewards = useStandardRewards;
    }

    @Override
    public void send(Player player, ActiveMob mob) {
        String mobName = mob.getType().getInternalName();

        logDebug("Starting processing topX rewards for mob: " + mobName +
                ", position: " + positionKey + ", player: " + player.getName());

        TopXRewardsProcessor.process(config, settingsKey, mobName, positionKey, player);

        if (useStandardRewards) {
            logDebug("Applying additional standard rewards for " + player.getName());
            StandardRewardProcessor.processStandardRewardsForPlayer(player, mob);
        }

        logDebug("Completed processing topX rewards for mob: " + mobName +
                ", position: " + positionKey + ", player: " + player.getName());
    }
}
