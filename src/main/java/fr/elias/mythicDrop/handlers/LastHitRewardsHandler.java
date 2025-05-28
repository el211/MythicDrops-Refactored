package fr.elias.mythicDrop.handlers;

import fr.elias.mythicDrop.rewards.LastHitReward;
import fr.elias.mythicDrop.rewards.RewardProcessor;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import static fr.elias.mythicDrop.MythicDrop.announcementConfig;
import static fr.elias.mythicDrop.announcers.AnnounceDamageRanking.announceDamageRanking;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class LastHitRewardsHandler {

    public static void handle(ActiveMob mob, MythicMobDeathEvent event) {
        // Identify the player who dealt the last hit
        Player lastHitter = (event.getKiller() instanceof Player) ? (Player) event.getKiller() : null;

        if (lastHitter != null) {
            logDebug("Assigning last-hit reward to " + lastHitter.getName());
            new RewardProcessor(new LastHitReward()).execute(lastHitter, mob);
        } else {
            logDebug("No valid last-hitter found.");
            return; // Exit if no killer is found
        }

        // Handle announcement logic
        handleAnnouncements(mob);
    }

    private static void handleAnnouncements(ActiveMob activeMob) {
        String mobName = activeMob.getType().getInternalName();
        boolean globalAnnounce = announcementConfig.getBoolean("announce-on-death", true);
        boolean specificMobAnnounce = announcementConfig.getBoolean("announce-specific-mob." + mobName, globalAnnounce);

        if (specificMobAnnounce) {
            logDebug("Announcements enabled for mob: " + mobName);
            announceDamageRanking(activeMob); // Announce the damage ranking
        } else {
            logDebug("Announcements disabled for mob: " + mobName);
        }
    }
}
