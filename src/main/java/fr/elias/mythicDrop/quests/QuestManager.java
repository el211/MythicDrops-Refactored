package fr.elias.mythicDrop.quests;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.quests.events.PlayerQuestCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class QuestManager {

    private final MythicDrop plugin;

    // All quests by quest ID
    private final Map<String, Quest> quests = new LinkedHashMap<>();
    // player UUID → (quest ID → kill count)
    private final Map<UUID, Map<String, Integer>> playerProgress = new HashMap<>();
    // player UUID → set of completed quest IDs
    private final Map<UUID, Set<String>> completedQuests = new HashMap<>();

    public QuestManager(MythicDrop plugin) {
        this.plugin = plugin;
    }

    // -------------------------------------------------------------------------
    // Loading
    // -------------------------------------------------------------------------

    public void loadQuests(FileConfiguration config) {
        quests.clear();

        ConfigurationSection root = config.getConfigurationSection("quests");
        if (root == null) return;

        for (String questID : root.getKeys(false)) {
            ConfigurationSection qs = root.getConfigurationSection(questID);
            if (qs == null) continue;

            String mobName = qs.getString("mob-name", "");
            String message = color(qs.getString("message", ""));

            // Quest item
            ConfigurationSection itemSec = qs.getConfigurationSection("quest-item");
            String material   = itemSec != null ? itemSec.getString("material", "PAPER") : "PAPER";
            int modelData      = itemSec != null ? itemSec.getInt("ModelData", 0) : 0;
            String displayName = color(itemSec != null ? itemSec.getString("displayname", questID) : questID);
            List<String> lore  = new ArrayList<>();
            if (itemSec != null && itemSec.isList("lore")) {
                for (String line : itemSec.getStringList("lore")) {
                    lore.add(color(line));
                }
            }

            // Conditions
            ConfigurationSection conditions = qs.getConfigurationSection("conditions");
            int requiredKills = conditions != null ? conditions.getInt("kill", 1) : 1;
            int slot          = conditions != null ? conditions.getInt("slot", 0) : 0;

            // Rewards
            Map<String, RewardSection> rewardSections = new LinkedHashMap<>();
            ConfigurationSection drops = qs.getConfigurationSection("reward.drops");
            if (drops != null) {
                for (String group : drops.getKeys(false)) {
                    ConfigurationSection groupSec = drops.getConfigurationSection(group);
                    if (groupSec == null) continue;

                    int guaranteed = groupSec.getInt("guaranteed-rewards", 0);
                    List<Reward> rewards = new ArrayList<>();

                    for (String key : groupSec.getKeys(false)) {
                        if (key.equals("guaranteed-rewards")) continue;
                        ConfigurationSection rewardSec = groupSec.getConfigurationSection(key);
                        if (rewardSec == null) continue;

                        String cmd    = rewardSec.getString("command", "");
                        double chance = rewardSec.getDouble("chance", 1.0);
                        String msg    = rewardSec.contains("message")
                                ? color(rewardSec.getString("message", "")) : null;

                        rewards.add(new Reward(cmd, chance, msg));
                    }

                    rewardSections.put(group, new RewardSection(guaranteed, rewards));
                }
            }

            quests.put(questID, new Quest(questID, mobName, requiredKills,
                    material, modelData, displayName, lore, slot, message, rewardSections));
        }
    }

    // -------------------------------------------------------------------------
    // Lookups
    // -------------------------------------------------------------------------

    public Optional<Quest> getQuestByMobName(String mobName) {
        return quests.values().stream()
                .filter(q -> q.getMobName().equalsIgnoreCase(mobName))
                .findFirst();
    }

    public int getProgress(UUID uuid, String questID) {
        return playerProgress.getOrDefault(uuid, Collections.emptyMap())
                             .getOrDefault(questID, 0);
    }

    public boolean isCompleted(UUID uuid, String questID) {
        return completedQuests.getOrDefault(uuid, Collections.emptySet()).contains(questID);
    }

    // -------------------------------------------------------------------------
    // Progress tracking
    // -------------------------------------------------------------------------

    public void updateQuestProgress(Player player, Quest quest) {
        UUID uuid    = player.getUniqueId();
        String qID   = quest.getQuestID();

        if (isCompleted(uuid, qID)) return;

        int current = playerProgress
                .computeIfAbsent(uuid, k -> new HashMap<>())
                .merge(qID, 1, Integer::sum);

        if (current >= quest.getRequiredKills()) {
            markQuestCompleted(player, qID);
        }
    }

    public void markQuestCompleted(Player player, String questID) {
        Quest quest = quests.get(questID);
        if (quest == null) return;

        UUID uuid = player.getUniqueId();
        completedQuests.computeIfAbsent(uuid, k -> new HashSet<>()).add(questID);
        playerProgress.getOrDefault(uuid, Collections.emptyMap()).remove(questID);

        // Completion message
        if (quest.getCompletionMessage() != null && !quest.getCompletionMessage().isEmpty()) {
            player.sendMessage(quest.getCompletionMessage());
        }

        // Rewards — try player's group, fall back to "default"
        String group = getRewardGroup(player);
        RewardSection section = quest.getRewardSections().get(group);
        if (section == null) section = quest.getRewardSections().get("default");
        if (section != null) runRewards(player, section);

        // Fire custom event so other systems can hook in
        Bukkit.getPluginManager().callEvent(new PlayerQuestCompleteEvent(player, quest));
    }

    // -------------------------------------------------------------------------
    // Reward execution
    // -------------------------------------------------------------------------

    private void runRewards(Player player, RewardSection section) {
        List<Reward> rewards  = section.getRewards();
        int guaranteed        = Math.min(section.getGuaranteedRewards(), rewards.size());
        Random rng            = new Random();

        // Guaranteed rewards (first N entries, no roll)
        for (int i = 0; i < guaranteed; i++) {
            executeReward(player, rewards.get(i));
        }

        // Chance-based rewards (remaining entries)
        for (int i = guaranteed; i < rewards.size(); i++) {
            Reward r = rewards.get(i);
            if (rng.nextDouble() < r.getChance()) {
                executeReward(player, r);
            }
        }
    }

    private void executeReward(Player player, Reward reward) {
        if (reward.getCommand() != null && !reward.getCommand().isEmpty()) {
            String cmd = reward.getCommand().replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        if (reward.getMessage() != null && !reward.getMessage().isEmpty()) {
            player.sendMessage(reward.getMessage());
        }
    }

    public String getRewardGroup(Player player) {
        return plugin.getPrimaryGroup(player);
    }

    // -------------------------------------------------------------------------
    // Accessors used by persistence and GUI
    // -------------------------------------------------------------------------

    /** Unmodifiable view of all loaded quests. */
    public Map<String, Quest> getQuests() {
        return Collections.unmodifiableMap(quests);
    }

    /** Mutable — used by {@link QuestDataPersistence} to populate on load. */
    public Map<UUID, Map<String, Integer>> getPlayerProgress() {
        return playerProgress;
    }

    /** Mutable — used by {@link QuestDataPersistence} to populate on load. */
    public Map<UUID, Set<String>> getCompletedQuests() {
        return completedQuests;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String color(String s) {
        return s == null ? "" : ChatColor.translateAlternateColorCodes('&', s);
    }
}
