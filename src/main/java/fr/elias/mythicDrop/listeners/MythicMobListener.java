package fr.elias.mythicDrop.listeners;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.announcers.AnnounceDamageRanking;
import fr.elias.mythicDrop.effects.EffectListener;
import fr.elias.mythicDrop.handlers.RewardProcessingHandler;
import fr.elias.mythicDrop.utils.DamageTracker;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.UUID;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class MythicMobListener implements Listener {

    private final MythicDrop plugin = MythicDrop.getInstance();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attackingPlayer = resolveAttackingPlayer(event.getDamager());
        if (attackingPlayer == null || event.getFinalDamage() <= 0) {
            return;
        }

        ActiveMob activeMob = MythicBukkit.inst().getMobManager().getActiveMob(event.getEntity().getUniqueId()).orElse(null);
        if (activeMob == null) {
            return;
        }

        DamageTracker.recordDamage(activeMob.getUniqueId(), attackingPlayer, event.getFinalDamage());
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        logDebug("MythicMobDeathEvent triggered.");

        ActiveMob activeMob = event.getMob();
        if (activeMob == null) {
            logDebug("Mob death event triggered, but the event has no ActiveMob.");
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

        logDamageRanking(activeMob);

        try {
            Player lastHitter = (event.getKiller() instanceof Player) ? (Player) event.getKiller() : null;
            RewardProcessingHandler.handleRewardProcessing(activeMob, lastHitter, event);
            AnnounceDamageRanking.announceDamageRanking(activeMob);
            EffectListener.playConfiguredEffects(activeMob, event.getEntity().getLocation());
        } catch (Exception e) {
            logDebug("Exception during reward processing for mob: " + mobName + " - " + e.getMessage());
            e.printStackTrace();
        } finally {
            plugin.getProcessedMobEvents().remove(mobId);
            DamageTracker.clearMob(mobId);
            logDebug("Finished processing MythicMobDeathEvent for mob: " + mobName);
        }
    }

    @EventHandler
    public void onMythicMobDespawn(MythicMobDespawnEvent event) {
        ActiveMob activeMob = event.getMob();
        if (activeMob == null) {
            return;
        }
        DamageTracker.clearMob(activeMob.getUniqueId());
    }

    private void logDamageRanking(ActiveMob activeMob) {
        List<DamageTracker.DamageEntry> ranking = DamageTracker.getSortedDamageRanking(activeMob.getUniqueId());
        if (ranking.isEmpty()) {
            logDebug("No tracked player damage found for mob: " + activeMob.getType().getInternalName());
            return;
        }

        logDebug("Tracked damage entries for mob " + activeMob.getType().getInternalName() + ": " + ranking.size());
        for (DamageTracker.DamageEntry damageEntry : ranking) {
            logDebug(" - Player: " + damageEntry.getPlayerName() + ", Damage: " + damageEntry.getDamage());
        }
    }

    private Player resolveAttackingPlayer(Entity damager) {
        if (damager instanceof Player player) {
            return player;
        }

        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            return player;
        }

        if (damager instanceof Tameable tameable && tameable.getOwner() instanceof Player player) {
            return player;
        }

        return null;
    }
}
