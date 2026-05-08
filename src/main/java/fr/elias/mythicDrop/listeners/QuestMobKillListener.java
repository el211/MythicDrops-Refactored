package fr.elias.mythicDrop.listeners;

import fr.elias.mythicDrop.quests.Quest;
import fr.elias.mythicDrop.quests.QuestManager;
import fr.elias.mythicDrop.utils.DamageTracker;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

/**
 * Entry point for quest kill tracking.
 *
 * Runs at LOWEST priority so it fires before MythicMobListener, which clears
 * DamageTracker in its finally-block. This guarantees tracker data is still
 * available for the indirect-kill fallback (bow, pets, etc.).
 */
public class QuestMobKillListener implements Listener {

    private final QuestManager questManager;

    public QuestMobKillListener(QuestManager questManager) {
        this.questManager = questManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        ActiveMob mob = event.getMob();
        if (mob == null) return;

        String mobName = mob.getType().getInternalName();
        Optional<Quest> questOpt = questManager.getQuestByMobName(mobName);
        if (questOpt.isEmpty()) return;

        Player killer = resolveKiller(event, mob);
        if (killer == null) return;

        questManager.updateQuestProgress(killer, questOpt.get());
    }

    /**
     * Returns the responsible player for this kill.
     * First tries the direct killer from the event (handles melee hits).
     * Falls back to the top entry in DamageTracker (handles bows, pets, splash
     * potions, etc.) — still valid because we run before DamageTracker is cleared.
     */
    private Player resolveKiller(MythicMobDeathEvent event, ActiveMob mob) {
        if (event.getKiller() instanceof Player player) {
            return player;
        }

        List<DamageTracker.DamageEntry> ranking =
                DamageTracker.getSortedDamageRanking(mob.getUniqueId());

        if (!ranking.isEmpty()) {
            return DamageTracker.getOnlinePlayer(ranking.get(0));
        }

        return null;
    }
}
