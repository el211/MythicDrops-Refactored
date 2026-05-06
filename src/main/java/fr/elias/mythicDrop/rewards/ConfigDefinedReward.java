package fr.elias.mythicDrop.rewards;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.processors.PlayerRewardsProcessor.processRewardsForPlayer;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class ConfigDefinedReward implements Reward {
    private final String mobName;

    public ConfigDefinedReward(String mobName) {
        this.mobName = mobName;
        ensureMobConfigExists();
    }

    @Override
    public void send(Player player, ActiveMob mob) {
        if (player == null) {
            logDebug("No valid player found for config-based reward.");
            return;
        }

        if (mob == null || mob.getType() == null) {
            logDebug("No valid MythicMob found for config-based reward.");
            return;
        }

        String resolvedMobName = mob.getType().getInternalName();
        if (!mobName.equalsIgnoreCase(resolvedMobName)) {
            logDebug("Config reward mob mismatch: expected " + mobName + " but received " + resolvedMobName + ".");
        }

        processRewardsForPlayer(mob, player, 1);
    }

    private void ensureMobConfigExists() {
        if (MythicDrop.getInstance().getConfig().getConfigurationSection(mobName) == null) {
            logDebug("No drops found for " + mobName + ". Skipping default generation.");
        }
    }
}
