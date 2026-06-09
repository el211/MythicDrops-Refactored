package fr.elias.mythicDrop.commands;

import fr.elias.mythicDrop.gui.MobsGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MMobsCommand implements CommandExecutor {

    private final MobsGUI mobsGUI;

    public MMobsCommand(MobsGUI mobsGUI) {
        this.mobsGUI = mobsGUI;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can browse MythicMobs.");
            return true;
        }
        mobsGUI.open(player);
        return true;
    }
}
