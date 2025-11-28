package fr.elias.mythicDrop.processors;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;

public class PlayerRewardsProcessor {

    public static void processRewardsForPlayer(ActiveMob activeMob, Player player, int position) {
        MythicDrop plugin = MythicDrop.getInstance();
        FileConfiguration config = plugin.getConfig();

        long startTime = System.currentTimeMillis();

        String mobName = activeMob.getType().getInternalName();
        logDebug("Starting reward processing for mob: " + mobName + ", player: " + player.getName() + ", position: " + position);

        if (!config.contains(mobName + ".drops")) {
            logDebug("No drop configuration found for mob: " + mobName);
            return;
        }

        ConfigurationSection mobDrops = config.getConfigurationSection(mobName + ".drops");
        if (mobDrops == null) {
            logDebug("Failed to retrieve drops section for mob: " + mobName);
            return;
        }

        boolean flexibleMode = config.getBoolean(mobName + ".flexible-reward-mode", false);
        logDebug("Flexible reward mode for " + mobName + ": " + flexibleMode);

        String primaryGroup = plugin.getPrimaryGroup(player);
        logDebug("Player " + player.getName() + " belongs to group: " + primaryGroup);

        ConfigurationSection groupDrops = mobDrops.contains(primaryGroup)
                ? mobDrops.getConfigurationSection(primaryGroup)
                : mobDrops.getConfigurationSection("default");

        if (groupDrops == null) {
            logDebug("No drop configuration for group: " + primaryGroup + " or default.");
            return;
        }

        List<String> rewardKeys = new ArrayList<>(groupDrops.getKeys(false));
        Collections.shuffle(rewardKeys);

        int guaranteedRewards = mobDrops.getInt("guaranteed-rewards", 0);
        if (rewardKeys.size() < guaranteedRewards) {
            guaranteedRewards = rewardKeys.size();
        }

        int dropsGiven = 0;

        for (int i = 0; i < rewardKeys.size(); i++) {
            String dropKey = rewardKeys.get(i);
            String command = groupDrops.getString(dropKey + ".command");
            String message = groupDrops.getString(dropKey + ".message");
            double chance = groupDrops.getDouble(dropKey + ".chance", 0.0);
            double roll = ThreadLocalRandom.current().nextDouble();

            boolean isGuaranteed = i < guaranteedRewards;

            logDebug("Evaluating drop: " + dropKey + " | Guaranteed: " + isGuaranteed + " | Chance: " + chance + " | Roll: " + roll);

            if (command == null || command.isEmpty()) {
                logDebug("Skipping " + dropKey + " due to missing command.");
                continue;
            }

            boolean giveReward = flexibleMode
                    ? (isGuaranteed || roll <= chance)
                    : (isGuaranteed || (dropsGiven < guaranteedRewards && roll <= chance));

            if (giveReward) {
                boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                logDebug("Executed command for " + dropKey + ": " + command + " | Success: " + success);

                if (message != null && !message.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    logDebug("Sent message for reward " + dropKey + ": " + message);
                }

                dropsGiven++;

                if (!flexibleMode && dropsGiven >= guaranteedRewards) {
                    break;
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logDebug("Finished reward processing for mob: " + mobName + ", player: " + player.getName() + ", position: " + position + " in " + duration + " ms.");
    }
}
