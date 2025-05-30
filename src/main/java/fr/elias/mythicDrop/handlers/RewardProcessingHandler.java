package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.rewards.ConfigDefinedReward;
import fr.elias.mythicDrop.rewards.LastHitReward;
import fr.elias.mythicDrop.rewards.MostDamageReward;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static fr.elias.mythicDrop.MythicDrop.announcementConfig;
import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.MythicDrop.top5Config;
import static fr.elias.mythicDrop.announcers.AnnounceDamageRanking.announceDamageRanking;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class RewardProcessingHandler {

    public static void handleRewardProcessing(ActiveMob activeMob, Player lastHitter, MythicMobDeathEvent event) {
        String mobName = activeMob.getType().getInternalName();
        logDebug("Processing rewards for mob: " + mobName);

        // Config-based reward (from config.yml)
        ConfigurationSection mobSection = MythicDrop.getInstance().getConfig().getConfigurationSection(mobName);

        if (mobSection != null && mobSection.isConfigurationSection("drops")) {
            logDebug("Mob " + mobName + " has custom drops defined in config.yml. Using ConfigDefinedReward.");
            FileConfiguration liveConfig = YamlConfiguration.loadConfiguration(new File(MythicDrop.getInstance().getDataFolder(), "config.yml"));
            new RewardProcessor(new ConfigDefinedReward(mobName, liveConfig)).execute(lastHitter, activeMob);
        } else if (top3Config.getStringList("rewardtop3").contains(mobName)) {
            logDebug("Delegating to Top3RewardsHandler for mob: " + mobName);
            Top3RewardsHandler.handle(activeMob);
        } else if (top5Config.getStringList("rewardtop5").contains(mobName)) {
            logDebug("Delegating to Top5RewardsHandler for mob: " + mobName);
            Top5RewardsHandler.handle(activeMob);
        } else if (MythicDrop.getInstance().getConfig().getBoolean("reward-processing.most-damage", false)
                && activeMob.hasThreatTable()) {
            logDebug("Delegating to MostDamageRewardsHandler...");
            MostDamageRewardsHandler.handle(activeMob);
        } else if (MythicDrop.getInstance().getConfig().getBoolean("reward-processing.last-hit", true)
                && lastHitter != null) {
            logDebug("Delegating to LastHitRewardsHandler...");
            LastHitRewardsHandler.handle(activeMob, event);
        } else {
            logDebug("No applicable reward strategy found for mob: " + mobName);
        }

        // Announce
        boolean globalAnnounce = announcementConfig.getBoolean("announce-on-death", true);
        boolean specificMobAnnounce = announcementConfig.getBoolean("announce-specific-mob." + mobName, globalAnnounce);
        if (specificMobAnnounce) {
            logDebug("Announcing damage ranking for mob: " + mobName);
            announceDamageRanking(activeMob);
        } else {
            logDebug("Announcements disabled for mob: " + mobName);
        }
    }

}
