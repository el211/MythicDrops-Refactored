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
