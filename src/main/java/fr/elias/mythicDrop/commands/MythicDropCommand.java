package fr.elias.mythicDrop.commands;

import fr.elias.mythicDrop.MythicDrop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static fr.elias.mythicDrop.MythicDrop.*;


public class MythicDropCommand implements CommandExecutor {

    MythicDrop plugin = MythicDrop.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, @NotNull String[] args) {
        if (label.equalsIgnoreCase("mythicdrop") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            // Reload main configuration file
            plugin.reloadConfig();

            // Reload custom configuration files
            announcementConfig.reload();
            debugConfig.reload();

            // Reload top3damage.yml and top5damage.yml
            top3Config.reload();
            top5Config.reload();

            // Notify the sender that the configurations have been reloaded
            sender.sendMessage(ChatColor.GREEN + "MythicDrop configuration reloaded, including top3damage.yml and top5damage.yml.");
            return true;
        }
        return false;
    }
}
