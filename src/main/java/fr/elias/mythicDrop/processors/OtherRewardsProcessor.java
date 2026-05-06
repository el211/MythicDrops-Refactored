package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

import static fr.elias.mythicDrop.MythicDrop.top3Config;
import static fr.elias.mythicDrop.MythicDrop.top5Config;
import static fr.elias.mythicDrop.utils.ConfigLookup.getGroupSection;
import static fr.elias.mythicDrop.utils.ConfigLookup.getSectionIgnoreCase;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class OtherRewardsProcessor {

    public static void processEveryoneElseRewards(String mobName, Player player, double playerDamage) {
        MythicDrop plugin = MythicDrop.getInstance();
        if (player == null) {
            logDebug("Player or player name is null. Skipping reward processing.");
            return;
        }

        logDebug("Processing rewards for everyone else who contributed for mob: " + mobName + ", player: " + player.getName());

        ConfigurationSection top5MobSection = getSectionIgnoreCase(top5Config, mobName);
        ConfigurationSection everyoneElseConfig = top5MobSection == null ? null : top5MobSection.getConfigurationSection("everyone-else-who-contributed");
        if (everyoneElseConfig == null) {
            ConfigurationSection top3MobSection = getSectionIgnoreCase(top3Config, mobName);
            everyoneElseConfig = top3MobSection == null ? null : top3MobSection.getConfigurationSection("everyone-else-who-contributed");
        }

        if (everyoneElseConfig == null) {
            logDebug("No valid 'everyone-else-who-contributed' section found for mob: " + mobName);
            return;
        }

        double minDamage = everyoneElseConfig.getDouble("min-damage", 0.0);
        if (playerDamage < minDamage) {
            logDebug("Player " + player.getName() + " did not meet the min-damage threshold (" + minDamage + "). Skipping rewards.");
            return;
        }

        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " primary group: " + primaryGroup);

        ConfigurationSection effectiveGroupDrops = getGroupSection(everyoneElseConfig, primaryGroup);
        if (effectiveGroupDrops == null) {
            logDebug("No valid drop configuration found for player group: " + primaryGroup + " or default.");
            return;
        }

        effectiveGroupDrops.getKeys(false).forEach(dropKey -> {
            double chance = effectiveGroupDrops.getDouble(dropKey + ".chance", 0.0);
            double roll = ThreadLocalRandom.current().nextDouble();
            logDebug("Processing reward " + dropKey + " for player: " + player.getName() + " | Roll: " + roll + " | Chance: " + chance);

            if (roll <= chance) {
                String command = effectiveGroupDrops.getString(dropKey + ".command");
                if (command == null || command.trim().isEmpty()) {
                    logDebug("Invalid or missing command for reward " + dropKey + " in configuration. Skipping.");
                    return;
                }

                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                logDebug("Executed reward command: " + command.replace("%player%", player.getName()) + " | Success: " + success);

                String message = effectiveGroupDrops.getString(dropKey + ".message");
                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent message to player: " + message);
                }
            } else {
                logDebug("Reward " + dropKey + " did not trigger. Chance threshold not met.");
            }
        });
    }
}
