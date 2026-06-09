package fr.elias.mythicDrop.gui;

import fr.elias.mythicDrop.quests.Quest;
import fr.elias.mythicDrop.quests.QuestManager;
import fr.elias.mythicDrop.quests.Reward;
import fr.elias.mythicDrop.quests.RewardSection;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestsSmartGUI {

    public static final String TITLE = ChatColor.DARK_PURPLE + "✦ Quests";

    private final InventoryManager manager;
    private final QuestManager questManager;

    public QuestsSmartGUI(InventoryManager manager, QuestManager questManager) {
        this.manager = manager;
        this.questManager = questManager;
    }

    public void open(Player player) {
        open(player, "all");
    }

    public void open(Player player, String filter) {
        SmartInventory.builder()
                .manager(manager)
                .id("mythicdrop-quests")
                .title(TITLE)
                .size(6, 9)
                .provider(new QuestsProvider(manager, questManager, filter, this))
                .build()
                .open(player);
    }

    public void openDetail(Player player, Quest quest, SmartInventory parent) {
        SmartInventory.builder()
                .manager(manager)
                .id("mythicdrop-quest-detail")
                .title(TITLE)
                .size(4, 9)
                .parent(parent)
                .provider(new QuestDetailProvider(manager, questManager, quest, parent))
                .build()
                .open(player);
    }

    static class QuestsProvider implements InventoryProvider {

        private final InventoryManager manager;
        private final QuestManager questManager;
        private final String filter;
        private final QuestsSmartGUI guiRef;

        QuestsProvider(InventoryManager manager, QuestManager questManager, String filter, QuestsSmartGUI guiRef) {
            this.manager = manager;
            this.questManager = questManager;
            this.filter = filter;
            this.guiRef = guiRef;
        }

        @Override
        public void init(Player player, InventoryContents contents) {
            // Fill all with gray glass
            contents.fill(ClickableItem.empty(grayGlass("")));

            UUID uuid = player.getUniqueId();

            for (Quest quest : questManager.getQuests().values()) {
                boolean completed = questManager.isCompleted(uuid, quest.getQuestID());

                // Apply filter
                if ("active".equals(filter) && completed) continue;
                if ("completed".equals(filter) && !completed) continue;

                // Parse material
                Material material;
                try {
                    material = Material.valueOf(quest.getQuestItemMaterial().toUpperCase());
                } catch (IllegalArgumentException | NullPointerException e) {
                    material = Material.PAPER;
                }

                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(quest.getDisplayName());

                    List<String> lore = new ArrayList<>(quest.getQuestLore());
                    lore.add("");

                    int current = questManager.getProgress(uuid, quest.getQuestID());
                    int required = quest.getRequiredKills();
                    if (completed) {
                        lore.add(ChatColor.GREEN + "✔ Completed");
                    } else {
                        lore.add(ChatColor.GRAY + "Progress: " + ChatColor.YELLOW + current + " / " + required);
                    }
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click for details"));

                    meta.setLore(lore);

                    if (quest.getModelData() > 0) {
                        meta.setCustomModelData(quest.getModelData());
                    }

                    item.setItemMeta(meta);
                }

                int slot = quest.getGuiSlot();
                int row = slot / 9;
                int col = slot % 9;

                // Make sure we don't place on bottom row (reserved for filters)
                if (row >= 5) row = 4;

                contents.set(row, col, ClickableItem.of(item, e -> {
                    SmartInventory parent = contents.inventory();
                    guiRef.openDetail(player, quest, parent);
                }));
            }

            // Filter buttons at row 5
            // ALL button at (5, 1)
            ItemStack allItem = new ItemStack(Material.BOOK);
            ItemMeta allMeta = allItem.getItemMeta();
            if (allMeta != null) {
                allMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eAll Quests"));
                List<String> allLore = new ArrayList<>();
                allLore.add(ChatColor.translateAlternateColorCodes('&', "&7Show all quests"));
                if ("all".equals(filter)) {
                    allLore.add(ChatColor.translateAlternateColorCodes('&', "&a► Currently selected"));
                }
                allMeta.setLore(allLore);
                allItem.setItemMeta(allMeta);
            }
            contents.set(5, 1, ClickableItem.of(allItem, e -> guiRef.open(player, "all")));

            // IN PROGRESS button at (5, 4)
            ItemStack activeItem = new ItemStack(Material.CLOCK);
            ItemMeta activeMeta = activeItem.getItemMeta();
            if (activeMeta != null) {
                activeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6In Progress"));
                List<String> activeLore = new ArrayList<>();
                activeLore.add(ChatColor.translateAlternateColorCodes('&', "&7Show active quests"));
                if ("active".equals(filter)) {
                    activeLore.add(ChatColor.translateAlternateColorCodes('&', "&a► Currently selected"));
                }
                activeMeta.setLore(activeLore);
                activeItem.setItemMeta(activeMeta);
            }
            contents.set(5, 4, ClickableItem.of(activeItem, e -> guiRef.open(player, "active")));

            // COMPLETED button at (5, 7)
            ItemStack completedItem = new ItemStack(Material.LIME_DYE);
            ItemMeta completedMeta = completedItem.getItemMeta();
            if (completedMeta != null) {
                completedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aCompleted"));
                List<String> completedLore = new ArrayList<>();
                completedLore.add(ChatColor.translateAlternateColorCodes('&', "&7Show completed quests"));
                if ("completed".equals(filter)) {
                    completedLore.add(ChatColor.translateAlternateColorCodes('&', "&a► Currently selected"));
                }
                completedMeta.setLore(completedLore);
                completedItem.setItemMeta(completedMeta);
            }
            contents.set(5, 7, ClickableItem.of(completedItem, e -> guiRef.open(player, "completed")));
        }

        @Override
        public void update(Player player, InventoryContents contents) {
            // No dynamic updates needed
        }
    }

    static class QuestDetailProvider implements InventoryProvider {

        private final InventoryManager manager;
        private final QuestManager questManager;
        private final Quest quest;
        private final SmartInventory parent;

        QuestDetailProvider(InventoryManager manager, QuestManager questManager, Quest quest, SmartInventory parent) {
            this.manager = manager;
            this.questManager = questManager;
            this.quest = quest;
            this.parent = parent;
        }

        @Override
        public void init(Player player, InventoryContents contents) {
            // Fill all with gray glass
            contents.fill(ClickableItem.empty(grayGlass("")));

            UUID uuid = player.getUniqueId();
            boolean completed = questManager.isCompleted(uuid, quest.getQuestID());
            int current = questManager.getProgress(uuid, quest.getQuestID());
            int required = quest.getRequiredKills();

            // Quest item at (0, 4)
            Material material;
            try {
                material = Material.valueOf(quest.getQuestItemMaterial().toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                material = Material.PAPER;
            }

            ItemStack questItem = new ItemStack(material);
            ItemMeta questMeta = questItem.getItemMeta();
            if (questMeta != null) {
                questMeta.setDisplayName(quest.getDisplayName());
                List<String> lore = new ArrayList<>(quest.getQuestLore());
                lore.add("");
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Mob: &f" + quest.getMobName()));
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Required Kills: &f" + required));
                lore.add("");
                if (completed) {
                    lore.add(ChatColor.GREEN + "✔ Completed");
                } else {
                    lore.add(ChatColor.GRAY + "Progress: " + ChatColor.YELLOW + current + " / " + required);
                }
                questMeta.setLore(lore);
                if (quest.getModelData() > 0) {
                    questMeta.setCustomModelData(quest.getModelData());
                }
                questItem.setItemMeta(questMeta);
            }
            contents.set(0, 4, ClickableItem.empty(questItem));

            // Progress bar item at (2, 4)
            ItemStack progressItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
            ItemMeta progressMeta = progressItem.getItemMeta();
            if (progressMeta != null) {
                progressMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eProgress"));
                List<String> progressLore = new ArrayList<>();
                progressLore.add(buildProgressBar(current, required));
                progressLore.add(ChatColor.YELLOW.toString() + current + ChatColor.GRAY + " / " + ChatColor.YELLOW + required);
                progressMeta.setLore(progressLore);
                progressItem.setItemMeta(progressMeta);
            }
            contents.set(2, 4, ClickableItem.empty(progressItem));

            // Reward items at row 1 for player's group
            List<Reward> rewards = getRewardsForPlayer(player, quest);
            for (int col = 0; col < Math.min(9, rewards.size()); col++) {
                Reward reward = rewards.get(col);
                Material rewardMaterial = parseCommandMaterial(reward.getCommand());

                ItemStack rewardItem = new ItemStack(rewardMaterial);
                ItemMeta rewardMeta = rewardItem.getItemMeta();
                if (rewardMeta != null) {
                    String displayName = reward.getMessage() != null && !reward.getMessage().isEmpty()
                            ? ChatColor.translateAlternateColorCodes('&', reward.getMessage())
                            : ChatColor.translateAlternateColorCodes('&', "&7Reward");
                    rewardMeta.setDisplayName(displayName);
                    List<String> rewardLore = new ArrayList<>();
                    rewardLore.add(ChatColor.translateAlternateColorCodes('&', "&7Cmd: &f" + reward.getCommand()));
                    rewardLore.add(ChatColor.translateAlternateColorCodes('&', "&7Chance: &f" + (int)(reward.getChance() * 100) + "%"));
                    rewardMeta.setLore(rewardLore);
                    rewardItem.setItemMeta(rewardMeta);
                }
                contents.set(1, col, ClickableItem.empty(rewardItem));
            }

            // Back button at (3, 4)
            ItemStack backItem = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backItem.getItemMeta();
            if (backMeta != null) {
                backMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cBack to Quests"));
                backItem.setItemMeta(backMeta);
            }
            contents.set(3, 4, ClickableItem.of(backItem, e -> parent.open(player)));
        }

        @Override
        public void update(Player player, InventoryContents contents) {
            // No dynamic updates needed
        }

        private List<Reward> getRewardsForPlayer(Player player, Quest quest) {
            String group = questManager.getRewardGroup(player);
            RewardSection section = quest.getRewardSections().get(group);
            if (section == null) {
                section = quest.getRewardSections().get("default");
            }
            if (section == null) {
                return new ArrayList<>();
            }
            return section.getRewards();
        }

        private Material parseCommandMaterial(String command) {
            if (command == null || command.isEmpty()) return Material.PAPER;
            String[] parts = command.split("\\s+");
            if (parts.length > 0 && parts[0].equalsIgnoreCase("money")) {
                return Material.GOLD_INGOT;
            }
            if (parts.length >= 3 && parts[0].equalsIgnoreCase("give")) {
                try {
                    return Material.valueOf(parts[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    return Material.PAPER;
                }
            }
            return Material.PAPER;
        }

        private String buildProgressBar(int current, int required) {
            int barLength = 20;
            int filled = required > 0 ? (int) Math.min(barLength, ((double) current / required) * barLength) : 0;
            StringBuilder bar = new StringBuilder();
            bar.append(ChatColor.GREEN.toString());
            for (int i = 0; i < filled; i++) {
                bar.append('▓');
            }
            bar.append(ChatColor.GRAY.toString());
            for (int i = filled; i < barLength; i++) {
                bar.append('░');
            }
            return bar.toString();
        }
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
