package fr.elias.mythicDrop.listeners;

import fr.elias.mythicDrop.quests.events.PlayerQuestCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Reacts to {@link PlayerQuestCompleteEvent} for any logic that sits on top of
 * base reward execution — broadcasts, effects, stat tracking, etc.
 */
public class QuestCompletionListener implements Listener {

    @EventHandler
    public void onQuestComplete(PlayerQuestCompleteEvent event) {
        String broadcast = ChatColor.GOLD + "✦ " + ChatColor.YELLOW + event.getPlayer().getName()
                + ChatColor.GREEN + " completed the quest "
                + ChatColor.YELLOW + event.getQuest().getDisplayName()
                + ChatColor.GREEN + "!";
        Bukkit.broadcastMessage(broadcast);
    }
}
