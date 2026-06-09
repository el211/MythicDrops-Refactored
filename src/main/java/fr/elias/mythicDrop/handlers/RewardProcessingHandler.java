package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.utils.MythicMobConfigReader;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.MythicDrop.top5Config;
import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.ConfigLookup.listContainsIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class RewardProcessingHandler {

    public static void handleRewardProcessing(ActiveMob activeMob, Player lastHitter, MythicMobDeathEvent event) {
        String mobName = activeMob.getType().getInternalName();
        logDebug("Processing rewards for mob: " + mobName);

        ConfigurationSection mobSection = getSectionIgnoreCase(MythicDrop.getInstance().getConfig(), mobName);
        boolean hasConfigDrops = mobSection != null && mobSection.isConfigurationSection("drops");
        boolean useMostDamage = MythicDrop.getInstance().getConfig().getBoolean("reward-processing.most-damage", false);
        boolean useLastHit = MythicDrop.getInstance().getConfig().getBoolean("reward-processing.last-hit", true);

        if (listContainsIgnoreCase(top3Config.getStringList("rewardtop3"), mobName)) {
            logDebug("Delegating to Top3RewardsHandler for mob: " + mobName);
            Top3RewardsHandler.handle(activeMob);
        } else if (listContainsIgnoreCase(top5Config.getStringList("rewardtop5"), mobName)) {
            logDebug("Delegating to Top5RewardsHandler for mob: " + mobName);
            Top5RewardsHandler.handle(activeMob);
        } else if (MythicDrop.getInstance().getTopXManager().getConfigForMob(mobName).isPresent()) {
            Map.Entry<Integer, YamlConfiguration> topXEntry = MythicDrop.getInstance().getTopXManager().getConfigForMob(mobName).get();
            logDebug("Delegating to TopXRewardsHandler for mob: " + mobName + " (topX=" + topXEntry.getKey() + ")");
            TopXRewardsHandler.handle(activeMob, topXEntry.getKey(), topXEntry.getValue());
        } else if (MythicMobConfigReader.getTopXFromMobConfig(mobName).isPresent()) {
            Map.Entry<Integer, YamlConfiguration> topXEntry = MythicMobConfigReader.getTopXFromMobConfig(mobName).get();
            logDebug("Delegating to TopXRewardsHandler (from mob config) for mob: " + mobName + " (topX=" + topXEntry.getKey() + ")");
            TopXRewardsHandler.handle(activeMob, topXEntry.getKey(), topXEntry.getValue());
        } else if (hasConfigDrops && useMostDamage) {
            logDebug("Mob " + mobName + " has config.yml drops and most-damage mode enabled. Delegating to MostDamageRewardsHandler.");
            MostDamageRewardsHandler.handle(activeMob);
        } else if (hasConfigDrops && useLastHit && lastHitter != null) {
            logDebug("Mob " + mobName + " has config.yml drops and last-hit mode enabled. Delegating to LastHitRewardsHandler.");
            LastHitRewardsHandler.handle(activeMob, lastHitter);
        } else if (hasConfigDrops) {
            logDebug("Mob " + mobName + " has config.yml drops, but no compatible reward-processing mode could be applied.");
        } else if (useMostDamage) {
            logDebug("Delegating to MostDamageRewardsHandler...");
            MostDamageRewardsHandler.handle(activeMob);
        } else if (useLastHit && lastHitter != null) {
            logDebug("Delegating to LastHitRewardsHandler...");
            LastHitRewardsHandler.handle(activeMob, lastHitter);
        } else {
            logDebug("No applicable reward strategy found for mob: " + mobName);
        }
    }
}
