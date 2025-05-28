package fr.elias.mythicDrop.listeners;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.handlers.RewardProcessingHandler;
import fr.elias.mythicDrop.handlers.Top3RewardsHandler;
import fr.elias.mythicDrop.handlers.Top5RewardsHandler;
import fr.elias.mythicDrop.rewards.*;
import fr.elias.mythicDrop.announcers.AnnounceDamageRanking;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.UUID;

import static fr.elias.mythicDrop.MythicDrop.*;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class MythicMobListener implements Listener {

    private final MythicDrop plugin = MythicDrop.getInstance();

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        logDebug("MythicMobDeathEvent triggered.");

        ActiveMob activeMob = MythicBukkit.inst().getMobManager()
                .getActiveMob(event.getEntity().getUniqueId())
                .orElse(null);

        if (activeMob == null) {
            logDebug("Mob death event triggered, but the entity is not a MythicMob.");
            return;
        }

        String mobName = activeMob.getType().getInternalName();
        if (mobName == null || mobName.isEmpty()) {
            logDebug("ActiveMob has no type or internal name. Aborting.");
            return;
        }

        UUID mobId = activeMob.getUniqueId();
        if (!plugin.getProcessedMobEvents().add(mobId)) {
            logDebug("Rewards for mob " + mobName + " have already been processed. Skipping.");
            return;
        }

        logThreatTable(activeMob);

        try {
            Player lastHitter = (event.getKiller() instanceof Player) ? (Player) event.getKiller() : null;
            RewardProcessingHandler.handleRewardProcessing(activeMob, lastHitter, event);
        } catch (Exception e) {
            logDebug("Exception during reward processing for mob: " + mobName + " - " + e.getMessage());
            e.printStackTrace();
        } finally {
            plugin.getProcessedMobEvents().remove(mobId);
            logDebug("Finished processing MythicMobDeathEvent for mob: " + mobName);
        }
    }


    private void logThreatTable(ActiveMob activeMob) {
        if (activeMob.hasThreatTable()) {
            logDebug("Threat table size for mob " + activeMob.getType().getInternalName() + ": " + activeMob.getThreatTable().size());
            for (AbstractEntity target : activeMob.getThreatTable().getAllThreatTargets()) {
                if (target == null) {
                    logDebug("Skipping null entity in threat table.");
                    continue;
                }
                logDebug(" - Entity: " + target.getName() + ", Threat: " + activeMob.getThreatTable().getThreat(target));
            }
        } else {
            logDebug("No Threat Table found for mob: " + activeMob.getType().getInternalName());
        }
    }


}
