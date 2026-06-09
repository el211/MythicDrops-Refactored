package fr.elias.mythicDrop.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DamageTracker {

    private static final Map<UUID, Map<UUID, DamageEntry>> DAMAGE_BY_MOB = new ConcurrentHashMap<>();

    private DamageTracker() {
    }

    public static void recordDamage(UUID mobId, Player player, double damage) {
        if (mobId == null || player == null || damage <= 0) {
            return;
        }

        DAMAGE_BY_MOB.computeIfAbsent(mobId, ignored -> new ConcurrentHashMap<>())
                .compute(player.getUniqueId(), (playerId, existingEntry) -> {
                    String playerName = player.getName() == null ? player.getUniqueId().toString() : player.getName();
                    if (existingEntry == null) {
                        return new DamageEntry(playerId, playerName, damage);
                    }
                    return existingEntry.addDamage(playerName, damage);
                });
    }

    public static List<DamageEntry> getSortedDamageRanking(UUID mobId) {
        if (mobId == null) {
            return Collections.emptyList();
        }

        Map<UUID, DamageEntry> mobDamage = DAMAGE_BY_MOB.get(mobId);
        if (mobDamage == null || mobDamage.isEmpty()) {
            return Collections.emptyList();
        }

        List<DamageEntry> ranking = new ArrayList<>(mobDamage.values());
        ranking.sort(Comparator.comparingDouble(DamageEntry::getDamage).reversed());
        return ranking;
    }

    public static Player getOnlinePlayer(DamageEntry damageEntry) {
        if (damageEntry == null) {
            return null;
        }
        return Bukkit.getPlayer(damageEntry.getPlayerId());
    }

    public static void clearMob(UUID mobId) {
        if (mobId != null) {
            DAMAGE_BY_MOB.remove(mobId);
        }
    }

    public static void clearAll() {
        DAMAGE_BY_MOB.clear();
    }

    /**
     * Aggregates damage across every currently tracked mob and returns a
     * list sorted by total damage descending. Useful for live leaderboard placeholders.
     */
    public static List<DamageEntry> getAggregatedRanking() {
        Map<UUID, double[]> totals = new java.util.HashMap<>();
        Map<UUID, String> names = new java.util.HashMap<>();
        for (Map<UUID, DamageEntry> mobDamage : DAMAGE_BY_MOB.values()) {
            for (DamageEntry entry : mobDamage.values()) {
                totals.merge(entry.getPlayerId(), new double[]{entry.getDamage()},
                        (a, b) -> { a[0] += b[0]; return a; });
                names.putIfAbsent(entry.getPlayerId(), entry.getPlayerName());
            }
        }
        List<DamageEntry> result = new ArrayList<>();
        for (Map.Entry<UUID, double[]> e : totals.entrySet()) {
            result.add(new DamageEntry(e.getKey(), names.get(e.getKey()), e.getValue()[0]));
        }
        result.sort(Comparator.comparingDouble(DamageEntry::getDamage).reversed());
        return result;
    }

    /**
     * Returns a player's total damage across all currently tracked mobs.
     */
    public static double getPlayerDamage(UUID playerId) {
        double total = 0;
        for (Map<UUID, DamageEntry> mobDamage : DAMAGE_BY_MOB.values()) {
            DamageEntry entry = mobDamage.get(playerId);
            if (entry != null) total += entry.getDamage();
        }
        return total;
    }

    public static final class DamageEntry {
        private final UUID playerId;
        private final String playerName;
        private final double damage;

        private DamageEntry(UUID playerId, String playerName, double damage) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.damage = damage;
        }

        private DamageEntry addDamage(String latestPlayerName, double additionalDamage) {
            return new DamageEntry(
                    playerId,
                    latestPlayerName == null || latestPlayerName.isEmpty() ? playerName : latestPlayerName,
                    damage + additionalDamage
            );
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public String getPlayerName() {
            return playerName;
        }

        public double getDamage() {
            return damage;
        }
    }
}
