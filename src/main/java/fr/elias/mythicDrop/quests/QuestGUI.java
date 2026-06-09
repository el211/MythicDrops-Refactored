package fr.elias.mythicDrop.quests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builds and opens the quest viewer inventory (/mquests).
 * The GUI is read-only — clicking items has no effect.
 */
public class QuestGUI {

    private static final int GUI_SIZE       = 54;
    public  static final String TITLE      = ChatColor.DARK_PURPLE + "✦ Quests";

    private final QuestManager questManager;

    public QuestGUI(QuestManager questManager) {
        this.questManager = questManager;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, TITLE);
        UUID uuid = player.getUniqueId();

        for (Quest quest : questManager.getQuests().values()) {
            int slot = quest.getGuiSlot();
            if (slot < 0 || slot >= GUI_SIZE) continue;

            Material material = parseMaterial(quest.getQuestItemMaterial());
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            meta.setDisplayName(quest.getDisplayName());
            if (quest.getModelData() > 0) {
                meta.setCustomModelData(quest.getModelData());
            }

            // Copy configured lore, then append live progress line
            List<String> lore = new ArrayList<>(quest.getQuestLore());
            lore.add("");

            boolean done = questManager.isCompleted(uuid, quest.getQuestID());
            if (done) {
                lore.add(ChatColor.GREEN + "✔ Completed");
            } else {
                int current = questManager.getProgress(uuid, quest.getQuestID());
                lore.add(ChatColor.GRAY + "Progress: "
                        + ChatColor.YELLOW + current + " / " + quest.getRequiredKills());
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        player.openInventory(inv);
    }

    private Material parseMaterial(String name) {
        if (name == null) return Material.PAPER;
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.PAPER;
        }
    }
}
