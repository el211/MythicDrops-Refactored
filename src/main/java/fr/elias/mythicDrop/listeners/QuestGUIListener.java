package fr.elias.mythicDrop.listeners;

import fr.elias.mythicDrop.gui.MobsGUI;
import fr.elias.mythicDrop.gui.QuestsSmartGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class QuestGUIListener implements Listener {

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith(QuestsSmartGUI.TITLE) || title.startsWith(MobsGUI.TITLE)) {
            event.setCancelled(true);
        }
    }
}
