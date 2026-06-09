package fr.elias.mythicDrop.placeholders;

import fr.elias.mythicDrop.MythicDrop;
import fr.elias.mythicDrop.quests.Quest;
import fr.elias.mythicDrop.quests.QuestManager;
import fr.elias.mythicDrop.utils.ArenaManager;
import fr.elias.mythicDrop.utils.DamageTracker;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * PlaceholderAPI expansion for MythicDropRefactored.
 *
 * ── Quest (per player) ──────────────────────────────────────────────────────
 *   %mythicdrop_quest_<id>_progress%   Current kill count toward the quest
 *   %mythicdrop_quest_<id>_required%   Required kills to complete the quest
 *   %mythicdrop_quest_<id>_remaining%  Kills still needed (0 when done)
 *   %mythicdrop_quest_<id>_percent%    Completion percentage (0-100)
 *   %mythicdrop_quest_<id>_completed%  true / false
 *   %mythicdrop_quest_<id>_name%       Display name (color codes stripped)
 *   %mythicdrop_quest_<id>_mob%        MythicMobs internal mob name
 *
 * ── Overall quest stats (per player) ────────────────────────────────────────
 *   %mythicdrop_quests_total%       Total number of configured quests
 *   %mythicdrop_quests_completed%   How many quests this player has finished
 *   %mythicdrop_quests_remaining%   How many quests are still left
 *   %mythicdrop_quests_percent%     Overall completion percentage (0-100)
 *
 * ── LuckPerms ───────────────────────────────────────────────────────────────
 *   %mythicdrop_group%   Player's primary LuckPerms group (requires online)
 *
 * ── Arenas ──────────────────────────────────────────────────────────────────
 *   %mythicdrop_arena_count%            Total configured arenas
 *   %mythicdrop_arena_live_count%       Arena mobs currently alive
 *   %mythicdrop_arena_<name>_exists%    true / false
 *   %mythicdrop_arena_<name>_mob%       Configured MythicMob type
 *   %mythicdrop_arena_<name>_world%     World the arena is in
 *   %mythicdrop_arena_<name>_alive%     Whether the arena mob is alive right now
 *
 * ── Live damage (active fight session) ──────────────────────────────────────
 *   %mythicdrop_live_damage%         Player's cumulative damage across all active mobs
 *   %mythicdrop_live_rank%           Player's current rank among damage dealers
 *   %mythicdrop_live_top<N>_name%    Name of the Nth top damage dealer  (e.g. live_top1_name)
 *   %mythicdrop_live_top<N>_damage%  Damage of the Nth top dealer       (e.g. live_top1_damage)
 */
public class MythicDropExpansion extends PlaceholderExpansion {

    private final MythicDrop plugin;

    public MythicDropExpansion(MythicDrop plugin) {
        this.plugin = plugin;
    }

    // -------------------------------------------------------------------------
    // Expansion metadata
    // -------------------------------------------------------------------------

    @Override
    public @NotNull String getIdentifier() {
        return "mythicdrop";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Elias";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        // Stays registered after /papi reload
        return true;
    }

    // -------------------------------------------------------------------------
    // Resolution
    // -------------------------------------------------------------------------

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        QuestManager qm = plugin.getQuestManager();
        ArenaManager am = ArenaManager.getInstance();

        // ── Global (no player required) ──────────────────────────────────────

        if (params.equals("arena_count")) {
            return String.valueOf(am.getArenaNames().size());
        }

        if (params.equals("arena_live_count")) {
            return String.valueOf(am.getLiveMobCount());
        }

        if (params.equals("quests_total")) {
            return String.valueOf(qm.getQuests().size());
        }

        // arena_<name>_<field>  — must come before the player block
        if (params.startsWith("arena_")) {
            String sub = params.substring("arena_".length()); // e.g. "myArena_exists"
            int sep = sub.lastIndexOf('_');
            if (sep > 0) {
                String arenaName = sub.substring(0, sep);
                String field = sub.substring(sep + 1);
                return resolveArena(am, arenaName, field);
            }
        }

        // live_top<N>_name / live_top<N>_damage  — no player required
        if (params.startsWith("live_top")) {
            String rest = params.substring("live_top".length()); // "1_name" or "2_damage"
            int sep = rest.indexOf('_');
            if (sep > 0) {
                try {
                    int rank = Integer.parseInt(rest.substring(0, sep));
                    String field = rest.substring(sep + 1);
                    List<DamageTracker.DamageEntry> ranking = DamageTracker.getAggregatedRanking();
                    if (rank < 1 || rank > ranking.size()) return "N/A";
                    DamageTracker.DamageEntry entry = ranking.get(rank - 1);
                    if (field.equals("name"))   return entry.getPlayerName();
                    if (field.equals("damage")) return String.format("%.0f", entry.getDamage());
                } catch (NumberFormatException ignored) { /* fall through */ }
            }
        }

        // ── Player-specific ──────────────────────────────────────────────────

        if (offlinePlayer == null) return "";

        UUID uuid = offlinePlayer.getUniqueId();
        Player player = offlinePlayer.getPlayer(); // null when offline

        // group
        if (params.equals("group")) {
            if (player == null) return "offline";
            return plugin.getPrimaryGroup(player);
        }

        // quests_completed / quests_remaining / quests_percent
        if (params.equals("quests_completed")) {
            return String.valueOf(countCompleted(qm, uuid));
        }
        if (params.equals("quests_remaining")) {
            return String.valueOf(qm.getQuests().size() - countCompleted(qm, uuid));
        }
        if (params.equals("quests_percent")) {
            int total = qm.getQuests().size();
            if (total == 0) return "100";
            return String.valueOf((int) ((countCompleted(qm, uuid) * 100.0) / total));
        }

        // live_damage / live_rank
        if (params.equals("live_damage")) {
            double dmg = DamageTracker.getPlayerDamage(uuid);
            return String.format("%.0f", dmg);
        }
        if (params.equals("live_rank")) {
            List<DamageTracker.DamageEntry> ranking = DamageTracker.getAggregatedRanking();
            for (int i = 0; i < ranking.size(); i++) {
                if (ranking.get(i).getPlayerId().equals(uuid)) return String.valueOf(i + 1);
            }
            return "N/A";
        }

        // quest_<questid>_<field>
        if (params.startsWith("quest_")) {
            String sub = params.substring("quest_".length()); // e.g. "boss1_progress"
            int sep = sub.lastIndexOf('_');
            if (sep > 0) {
                String questId = sub.substring(0, sep);
                String field = sub.substring(sep + 1);
                return resolveQuest(qm, uuid, questId, field);
            }
        }

        return null; // unrecognized placeholder — PAPI will leave it as-is
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private @Nullable String resolveQuest(QuestManager qm, UUID uuid, String questId, String field) {
        Map<String, Quest> quests = qm.getQuests();
        Quest quest = quests.get(questId);
        if (quest == null) return "unknown_quest";

        boolean completed = qm.isCompleted(uuid, questId);
        int progress  = qm.getProgress(uuid, questId);
        int required  = quest.getRequiredKills();
        int effective = completed ? required : progress;

        switch (field) {
            case "progress":  return String.valueOf(effective);
            case "required":  return String.valueOf(required);
            case "remaining": return completed ? "0" : String.valueOf(Math.max(0, required - progress));
            case "percent": {
                if (required == 0) return "100";
                return String.valueOf(Math.min(100, (int) ((effective * 100.0) / required)));
            }
            case "completed": return String.valueOf(completed);
            case "name":      return ChatColor.stripColor(quest.getDisplayName());
            case "mob":       return quest.getMobName();
            default:          return null;
        }
    }

    private @Nullable String resolveArena(ArenaManager am, String arenaName, String field) {
        switch (field) {
            case "exists": return String.valueOf(am.arenaExists(arenaName));
            case "alive":  return String.valueOf(am.isMobAlive(arenaName));
            case "mob":    return am.arenaExists(arenaName) ? am.getArenaMob(arenaName)   : "unknown";
            case "world":  return am.arenaExists(arenaName) ? am.getArenaWorld(arenaName) : "unknown";
            default:       return null;
        }
    }

    private int countCompleted(QuestManager qm, UUID uuid) {
        Set<String> done = qm.getCompletedQuests().getOrDefault(uuid, Collections.emptySet());
        return done.size();
    }
}
