package fr.elias.mythicDrop.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobsGUI {

    public static final String TITLE = ChatColor.DARK_GREEN + "✦ MythicMobs";

    private final InventoryManager manager;

    public MobsGUI(InventoryManager manager) {
        this.manager = manager;
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void open(Player player, int page) {
        SmartInventory.builder()
                .manager(manager)
                .id("mythicdrop-mobs")
                .title(TITLE)
                .size(6, 9)
                .provider(new MobsProvider(manager, page))
                .build()
                .open(player);
    }

    static class MobsProvider implements InventoryProvider {

        private final InventoryManager manager;
        private final int page;

        MobsProvider(InventoryManager manager, int page) {
            this.manager = manager;
            this.page = page;
        }

        @Override
        public void init(Player player, InventoryContents contents) {
            // Fill bottom row with gray glass pane
            ItemStack filler = grayGlass("");
            for (int col = 1; col <= 7; col++) {
                contents.set(5, col, ClickableItem.empty(filler));
            }

            // Load and sort mobs
            List<MythicMob> mobs = new ArrayList<>(MythicBukkit.inst().getMobManager().getMobTypes());
            mobs.sort((a, b) -> a.getInternalName().compareToIgnoreCase(b.getInternalName()));

            // Build clickable items
            ClickableItem[] items = new ClickableItem[mobs.size()];
            for (int i = 0; i < mobs.size(); i++) {
                MythicMob mob = mobs.get(i);
                Material material = entityTypeToMaterial(mob.getEntityTypeString());

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.YELLOW + mob.getInternalName());
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&7Display: &f" + mob.getDisplayName().toString()));
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&7Type: &f" + mob.getEntityTypeString()));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }

                final MythicMob finalMob = mob;
                items[i] = ClickableItem.of(item, e -> {
                    SmartInventory parent = contents.inventory();
                    SmartInventory detail = SmartInventory.builder()
                            .manager(manager)
                            .id("mythicdrop-mob-detail-" + finalMob.getInternalName())
                            .title(ChatColor.DARK_GREEN + "✦ " + finalMob.getInternalName())
                            .size(3, 9)
                            .provider(new MobDetailProvider(manager, finalMob, parent))
                            .parent(parent)
                            .build();
                    detail.open(player);
                });
            }

            Pagination pagination = contents.pagination();
            pagination.setItems(items);
            pagination.setItemsPerPage(45);
            pagination.page(page);

            SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
            pagination.addToIterator(iterator);

            // Previous page button
            if (!pagination.isFirst()) {
                ItemStack prevItem = new ItemStack(Material.ARROW);
                ItemMeta prevMeta = prevItem.getItemMeta();
                if (prevMeta != null) {
                    prevMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&ePrevious Page"));
                    prevItem.setItemMeta(prevMeta);
                }
                contents.set(5, 0, ClickableItem.of(prevItem, e -> new MobsGUI(manager).open(player, page - 1)));
            } else {
                contents.set(5, 0, ClickableItem.empty(grayGlass("")));
            }

            // Next page button
            if (!pagination.isLast()) {
                ItemStack nextItem = new ItemStack(Material.ARROW);
                ItemMeta nextMeta = nextItem.getItemMeta();
                if (nextMeta != null) {
                    nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eNext Page"));
                    nextItem.setItemMeta(nextMeta);
                }
                contents.set(5, 8, ClickableItem.of(nextItem, e -> new MobsGUI(manager).open(player, page + 1)));
            } else {
                contents.set(5, 8, ClickableItem.empty(grayGlass("")));
            }
        }

        @Override
        public void update(Player player, InventoryContents contents) {
            // No dynamic updates needed
        }

        private static ItemStack grayGlass(String name) {
            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
            return item;
        }
    }

    static class MobDetailProvider implements InventoryProvider {

        private final InventoryManager manager;
        private final MythicMob mob;
        private final SmartInventory parent;

        MobDetailProvider(InventoryManager manager, MythicMob mob, SmartInventory parent) {
            this.manager = manager;
            this.mob = mob;
            this.parent = parent;
        }

        @Override
        public void init(Player player, InventoryContents contents) {
            // Fill all with gray glass
            ItemStack filler = grayGlass("");
            contents.fill(ClickableItem.empty(filler));

            String internalName = mob.getInternalName();
            String displayName = mob.getDisplayName().toString();
            String entityType = mob.getEntityTypeString();

            // Center item at (1, 4)
            Material material = entityTypeToMaterial(entityType);
            ItemStack mobItem = new ItemStack(material);
            ItemMeta meta = mobItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + internalName);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Internal Name: &f" + internalName));
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Display Name: &f" + displayName));
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Entity Type: &f" + entityType));
                meta.setLore(lore);
                mobItem.setItemMeta(meta);
            }
            contents.set(1, 4, ClickableItem.empty(mobItem));

            // Back button at (2, 4)
            ItemStack backItem = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backItem.getItemMeta();
            if (backMeta != null) {
                backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cBack"));
                backItem.setItemMeta(backMeta);
            }
            contents.set(2, 4, ClickableItem.of(backItem, e -> parent.open(player)));
        }

        @Override
        public void update(Player player, InventoryContents contents) {
            // No dynamic updates needed
        }

        private static ItemStack grayGlass(String name) {
            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
            return item;
        }
    }

    private static Material entityTypeToMaterial(String entityType) {
        if (entityType == null) return Material.PAPER;
        switch (entityType.toUpperCase()) {
            case "ZOMBIE":            return Material.ROTTEN_FLESH;
            case "SKELETON":          return Material.BONE;
            case "SPIDER":            return Material.SPIDER_EYE;
            case "CAVE_SPIDER":       return Material.COBWEB;
            case "WITHER_SKELETON":   return Material.WITHER_SKELETON_SKULL;
            case "CREEPER":           return Material.GUNPOWDER;
            case "ENDERMAN":          return Material.ENDER_PEARL;
            case "BLAZE":             return Material.BLAZE_ROD;
            case "GHAST":             return Material.GHAST_TEAR;
            case "ZOMBIFIED_PIGLIN":  return Material.GOLD_NUGGET;
            case "ZOMBIE_PIGMAN":     return Material.GOLD_NUGGET;
            case "WITCH":             return Material.GLASS_BOTTLE;
            case "WITHER":            return Material.NETHER_STAR;
            case "ENDER_DRAGON":      return Material.DRAGON_EGG;
            case "SLIME":             return Material.SLIME_BALL;
            case "MAGMA_CUBE":        return Material.MAGMA_CREAM;
            case "GIANT":             return Material.IRON_SWORD;
            case "ELDER_GUARDIAN":    return Material.PRISMARINE_SHARD;
            case "GUARDIAN":          return Material.PRISMARINE_SHARD;
            case "HOGLIN":            return Material.PORKCHOP;
            case "PIGLIN":            return Material.GOLD_INGOT;
            case "PIGLIN_BRUTE":      return Material.GOLDEN_SWORD;
            case "VILLAGER":          return Material.EMERALD;
            case "STRIDER":           return Material.WARPED_FUNGUS;
            default:                  return Material.PAPER;
        }
    }
}
