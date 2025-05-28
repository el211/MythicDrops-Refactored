package fr.elias.mythicDrop.announcers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fr.elias.mythicDrop.MythicDrop.*;
import static fr.elias.mythicDrop.processors.OtherRewardsProcessor.processEveryoneElseRewards;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class AnnounceDamageRanking {

    /**
     * Announce the list of players who inflicted the most damage on the mob.
     */
    public static void announceDamageRanking(ActiveMob activeMob) {
        if (announcementConfig == null) {
            logDebug("announcementConfig is null; announcements cannot proceed.");
            return;
        }
        String mobName = activeMob.getType().getInternalName();
        logDebug("Starting damage ranking announcement for mob: " + mobName);

        // Check global and specific mob announcement settings
        boolean globalAnnounce = announcementConfig.getBoolean("announce-on-death", true);
        ConfigurationSection mobSpecificConfig = announcementConfig.getConfigurationSection("announce-specific-mob." + mobName);
        boolean specificMobAnnounce = mobSpecificConfig != null && mobSpecificConfig.getBoolean("announce", globalAnnounce);

        logDebug("Global announce-on-death enabled: " + globalAnnounce);
        logDebug("Specific mob announcement enabled for " + mobName + ": " + specificMobAnnounce);

        // Prioritize specific mob settings; skip announcement if disabled
        if (!specificMobAnnounce) {
            logDebug("Announcements are disabled for mob: " + mobName);
            return; // Skip announcements for this mob
        }

        // Check if threat table exists and has players
        if (!activeMob.hasThreatTable() || activeMob.getThreatTable().getAllThreatTargets().isEmpty()) {
            String noPlayersMessage = ChatColor.translateAlternateColorCodes('&',
                    mobSpecificConfig.getString("messages.no-players", "&cNo players contributed to %BOSSNAME%.")
                            .replace("%BOSSNAME%", mobName));
            Bukkit.broadcastMessage(noPlayersMessage);
            logDebug("No players contributed damage to the mob: " + mobName);
            return;
        }

        // Announce header message
        String header = ChatColor.translateAlternateColorCodes('&',
                mobSpecificConfig.getString("messages.header", "&aLIST OF PLAYERS WHO HAVE INFLICTED THE MOST DAMAGE ON %BOSSNAME%:")
                        .replace("%BOSSNAME%", mobName));
        Bukkit.broadcastMessage(header);
        logDebug("Broadcasted header message: " + header);

        // Sort threat table by damage
        List<AbstractEntity> sortedRanking = activeMob.getThreatTable().getAllThreatTargets().stream()
                .sorted(Comparator.comparingDouble(activeMob.getThreatTable()::getThreat).reversed())
                .collect(Collectors.toList());
        logDebug("Sorted damage ranking for mob: " + mobName + ". Total contributors: " + sortedRanking.size());

        // Check if the mob is configured for Top 3 or Top 5 rewards
        boolean isTop3RewardMob = top3Config.getStringList("rewardtop3").contains(mobName);
        boolean isTop5RewardMob = top5Config.getStringList("rewardtop5").contains(mobName);

        // Announce Top 3 or Top 5 contributors based on configuration
        int maxEntries = isTop3RewardMob ? 3 : isTop5RewardMob ? 5 : 0;
        if (maxEntries > 0) {
            logDebug("Mob " + mobName + " is configured for Top " + maxEntries + " rewards. Announcing players...");
            for (int i = 0; i < Math.min(maxEntries, sortedRanking.size()); i++) {
                AbstractEntity entity = sortedRanking.get(i);
                if (entity.isPlayer()) {
                    Player player = (Player) entity.asPlayer().getBukkitEntity();
                    double damage = activeMob.getThreatTable().getThreat(entity);

                    String entry = ChatColor.translateAlternateColorCodes('&',
                            mobSpecificConfig.getString("messages.entry", "&8#%position% &a%player% &f(%damage% DAMAGE)")
                                    .replace("%position%", String.valueOf(i + 1))
                                    .replace("%player%", player.getName())
                                    .replace("%damage%", String.valueOf((int) damage)));

                    Bukkit.broadcastMessage(entry);
                    logDebug("Broadcasted entry for position " + (i + 1) + ": " + entry);
                } else {
                    logDebug("Entity at position " + (i + 1) + " is not a player. Skipping.");
                }
            }
        } else {
            logDebug("Mob " + mobName + " is not configured for Top 3 or Top 5 rewards. Skipping Top rankings.");
        }

        // Reward everyone else who contributed based on configuration
        ConfigurationSection everyoneElseConfig = isTop5RewardMob
                ? top5Config.getConfigurationSection(mobName + ".everyone-else-who-contributed")
                : top3Config.getConfigurationSection(mobName + ".everyone-else-who-contributed");

        if (everyoneElseConfig != null) {
            double minDamage = everyoneElseConfig.getDouble("min-damage", 0.0); // Default to 0.0
            logDebug("Processing rewards for everyone who contributed with minimum damage: " + minDamage);

            for (int i = maxEntries; i < sortedRanking.size(); i++) {
                AbstractEntity entity = sortedRanking.get(i);
                if (entity.isPlayer()) {
                    Player player = (Player) entity.asPlayer().getBukkitEntity();
                    double playerDamage = activeMob.getThreatTable().getThreat(entity);

                    if (playerDamage >= minDamage) {
                        logDebug("Rewarding player " + player.getName() + " for contributing damage: " + playerDamage);
                        processEveryoneElseRewards(mobName, player, playerDamage);
                    } else {
                        logDebug("Player " + player.getName() + " did not meet the min-damage threshold (" + minDamage + "). Skipping.");
                    }
                } else {
                    logDebug("Entity in contributors list is not a player. Skipping.");
                }
            }
        } else {
            logDebug("No 'everyone-else-who-contributed' configuration found for mob: " + mobName);
        }

        logDebug("Finished processing announcements for mob: " + mobName);
    }
}
