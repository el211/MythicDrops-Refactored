package fr.elias.mythicDrop.announcers;

import fr.elias.mythicDrop.utils.DamageTracker;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

import static fr.elias.mythicDrop.MythicDrop.announcementConfig;
import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.MythicDrop.top5Config;
import static fr.elias.mythicDrop.utils.ConfigLookup.listContainsIgnoreCase;
import static fr.elias.mythicDrop.utils.ConfigLookup.resolveKeyIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class AnnounceDamageRanking {

    public static void announceDamageRanking(ActiveMob activeMob) {
        if (announcementConfig == null) {
            logDebug("announcementConfig is null; announcements cannot proceed.");
            return;
        }

        String mobName = activeMob.getType().getInternalName();
        logDebug("Starting damage ranking announcement for mob: " + mobName);

        boolean globalAnnounce = announcementConfig.getBoolean("announce-on-death", true);
        ConfigurationSection announceSpecificMobSection = announcementConfig.getConfigurationSection("announce-specific-mob");
        String resolvedMobOverrideKey = resolveKeyIgnoreCase(announceSpecificMobSection, mobName);

        Object mobOverride = resolvedMobOverrideKey == null ? null : announceSpecificMobSection.get(resolvedMobOverrideKey);
        ConfigurationSection mobOverrideSection = mobOverride instanceof ConfigurationSection
                ? announceSpecificMobSection.getConfigurationSection(resolvedMobOverrideKey)
                : null;
        boolean specificMobAnnounce = globalAnnounce;

        if (mobOverride instanceof Boolean) {
            specificMobAnnounce = (Boolean) mobOverride;
        } else if (mobOverrideSection != null) {
            specificMobAnnounce = mobOverrideSection.getBoolean("announce", globalAnnounce);
        }

        ConfigurationSection defaultMessages = announcementConfig.getConfigurationSection("messages");
        ConfigurationSection overrideMessages = mobOverrideSection == null ? null : mobOverrideSection.getConfigurationSection("messages");

        logDebug("Global announce-on-death enabled: " + globalAnnounce);
        logDebug("Specific mob announcement enabled for " + mobName + ": " + specificMobAnnounce);

        if (!specificMobAnnounce) {
            logDebug("Announcements are disabled for mob: " + mobName);
            return;
        }

        List<DamageTracker.DamageEntry> sortedRanking = DamageTracker.getSortedDamageRanking(activeMob.getUniqueId());
        if (sortedRanking.isEmpty()) {
            String noPlayersMessage = ChatColor.translateAlternateColorCodes('&',
                    getMessage(overrideMessages, defaultMessages, "no-players", "&cNo players contributed to %BOSSNAME%.")
                            .replace("%BOSSNAME%", mobName)
            );
            Bukkit.broadcastMessage(noPlayersMessage);
            logDebug("No players contributed damage to the mob: " + mobName);
            return;
        }

        String header = ChatColor.translateAlternateColorCodes('&',
                getMessage(overrideMessages, defaultMessages, "header", "&aLIST OF PLAYERS WHO HAVE INFLICTED THE MOST DAMAGE ON %BOSSNAME%:")
                        .replace("%BOSSNAME%", mobName));
        Bukkit.broadcastMessage(header);
        logDebug("Broadcasted header message: " + header);

        logDebug("Sorted damage ranking for mob: " + mobName + ". Total contributors: " + sortedRanking.size());

        boolean isTop3RewardMob = listContainsIgnoreCase(top3Config.getStringList("rewardtop3"), mobName);
        boolean isTop5RewardMob = listContainsIgnoreCase(top5Config.getStringList("rewardtop5"), mobName);

        int maxEntries = isTop3RewardMob ? 3 : isTop5RewardMob ? 5 : sortedRanking.size();
        logDebug("Announcing up to " + maxEntries + " contributors for mob " + mobName + ".");

        for (int i = 0; i < Math.min(maxEntries, sortedRanking.size()); i++) {
            DamageTracker.DamageEntry damageEntry = sortedRanking.get(i);
            String entry = ChatColor.translateAlternateColorCodes('&',
                    getMessage(overrideMessages, defaultMessages, "entry", "&8#%position% &a%player% &f(%damage% DAMAGE)")
                            .replace("%position%", String.valueOf(i + 1))
                            .replace("%player%", damageEntry.getPlayerName())
                            .replace("%damage%", String.valueOf((int) damageEntry.getDamage()))
            );

            Bukkit.broadcastMessage(entry);
            logDebug("Broadcasted entry for position " + (i + 1) + ": " + entry);
        }

        logDebug("Finished processing announcements for mob: " + mobName);
    }

    private static String getMessage(ConfigurationSection overrideMessages,
                                     ConfigurationSection defaultMessages,
                                     String key,
                                     String fallback) {
        if (overrideMessages != null && overrideMessages.contains(key)) {
            return overrideMessages.getString(key, fallback);
        }
        if (defaultMessages != null) {
            return defaultMessages.getString(key, fallback);
        }
        return fallback;
    }
}
