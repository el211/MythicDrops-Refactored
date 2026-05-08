package fr.elias.mythicDrop.commands;

import fr.elias.mythicDrop.utils.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MArenaCommand implements CommandExecutor {

    private static final String PREFIX = ChatColor.GOLD + "[MArena] " + ChatColor.RESET;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            // ------------------------------------------------------------------
            // /marena setspawn <name> <mob> <respawn> <despawn> <radius>
            // ------------------------------------------------------------------
            case "setspawn" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(PREFIX + ChatColor.RED + "Only players can use setspawn (requires a location).");
                    return true;
                }
                if (args.length < 6) {
                    sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /marena setspawn <name> <mob> <respawn> <despawn> <radius>");
                    return true;
                }

                String name = args[1];
                String mob = args[2];
                int respawn, despawn, radius;

                try {
                    respawn = Integer.parseInt(args[3]);
                    despawn = Integer.parseInt(args[4]);
                    radius  = Integer.parseInt(args[5]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PREFIX + ChatColor.RED + "respawn, despawn and radius must be whole numbers.");
                    return true;
                }

                if (respawn < 0 || despawn < 0 || radius <= 0) {
                    sender.sendMessage(PREFIX + ChatColor.RED + "respawn/despawn must be ≥ 0 and radius must be > 0.");
                    return true;
                }

                ArenaManager.getInstance().setArena(name, player.getLocation(), mob, respawn, despawn, radius);
                sender.sendMessage(PREFIX + ChatColor.GREEN + "Arena '" + ChatColor.YELLOW + name
                        + ChatColor.GREEN + "' saved. Mob '" + ChatColor.YELLOW + mob
                        + ChatColor.GREEN + "' will spawn at your location.");
            }

            // ------------------------------------------------------------------
            // /marena delete <name>
            // ------------------------------------------------------------------
            case "delete" -> {
                if (args.length < 2) {
                    sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /marena delete <name>");
                    return true;
                }
                String name = args[1];
                if (ArenaManager.getInstance().deleteArena(name)) {
                    sender.sendMessage(PREFIX + ChatColor.GREEN + "Arena '" + ChatColor.YELLOW + name
                            + ChatColor.GREEN + "' deleted.");
                } else {
                    sender.sendMessage(PREFIX + ChatColor.RED + "No arena named '" + name + "' exists.");
                }
            }

            // ------------------------------------------------------------------
            // /marena list
            // ------------------------------------------------------------------
            case "list" -> {
                Set<String> names = ArenaManager.getInstance().getArenaNames();
                if (names.isEmpty()) {
                    sender.sendMessage(PREFIX + ChatColor.GRAY + "No arenas configured.");
                    return true;
                }
                sender.sendMessage(PREFIX + ChatColor.GOLD + "Configured arenas (" + names.size() + "):");
                for (String name : names) {
                    String info = ArenaManager.getInstance().getArenaInfo(name);
                    // Replace our own colour codes so they render in chat
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  " + info));
                }
            }

            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(PREFIX + ChatColor.GOLD + "Commands:");
        sender.sendMessage(ChatColor.YELLOW + "  /marena setspawn <name> <mob> <respawn> <despawn> <radius>");
        sender.sendMessage(ChatColor.YELLOW + "  /marena delete <name>");
        sender.sendMessage(ChatColor.YELLOW + "  /marena list");
    }
}
