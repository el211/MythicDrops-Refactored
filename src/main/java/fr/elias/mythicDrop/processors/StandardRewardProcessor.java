package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static fr.elias.mythicDrop.processors.PlayerRewardsProcessor.processRewardsForPlayer;
import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class StandardRewardProcessor {

    public static void processStandardRewardsForPlayer(Player player, ActiveMob mob) {
        String mobName = mob.getType().getInternalName();
        FileConfiguration config = MythicDrop.getInstance().getConfig();
        ConfigurationSection mobSection = getSectionIgnoreCase(config, mobName);
        ConfigurationSection standardRewards = mobSection == null ? null : mobSection.getConfigurationSection("standard-rewards");

        if (standardRewards != null) {
            logDebug("Processing legacy standard-rewards section for mob: " + mobName);
            processLegacyStandardRewards(player, standardRewards);
            return;
        }

        logDebug("No standard-rewards section found for mob " + mobName + ". Falling back to config.yml drops.");
        processRewardsForPlayer(mob, player, 1);
    }

    private static void processLegacyStandardRewards(Player player, ConfigurationSection standardRewards) {
        for (String dropKey : standardRewards.getKeys(false)) {
            String command = standardRewards.getString(dropKey + ".command");
            String message = standardRewards.getString(dropKey + ".message");
            double chance = standardRewards.getDouble(dropKey + ".chance", 0.0);

            if (command == null || command.trim().isEmpty()) {
                logDebug("Skipping drop " + dropKey + ": Missing command.");
                continue;
            }

            double roll = Math.random();
            logDebug("Evaluating standard drop: " + dropKey + " | Roll=" + roll + " | Chance=" + chance);

            if (roll <= chance) {
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                logDebug("Executed standard drop command: " + command + " | Success: " + success);

                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent standard drop message: " + message);
                }
            } else {
                logDebug("Drop " + dropKey + " did not pass chance check.");
            }
        }
    }
}
