package fr.elias.mythicDrop.listeners;

import fr.elias.mythicDrop.quests.QuestGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Prevents players from taking or moving items out of the quest viewer GUI.
 */
public class QuestGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (QuestGUI.TITLE.equals(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (QuestGUI.TITLE.equals(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }
}
