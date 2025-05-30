package fr.elias.mythicDrop.commands;

import fr.elias.mythicDrop.MythicDrop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static fr.elias.mythicDrop.MythicDrop.*;

public class MythicDropCommand implements CommandExecutor {

    private final MythicDrop plugin = MythicDrop.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            // Reload Bukkit config system (plugin.yml linked config)
            plugin.reloadConfig();

            // Reload custom config wrapper for config.yml
            plugin.config = new fr.elias.mythicDrop.utils.Config("config.yml");

            // Reload other config files
            announcementConfig.reload();
            debugConfig.reload();
            top3Config.reload();
            top5Config.reload();
            effectsConfig.reload(); // <-- Add this

            // Confirm to user
            sender.sendMessage(ChatColor.GREEN + "MythicDrop configuration reloaded successfully.");
            return true;
        }

        return false;
    }
}
