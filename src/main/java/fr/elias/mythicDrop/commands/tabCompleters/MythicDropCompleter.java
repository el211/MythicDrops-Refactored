package fr.elias.mythicDrop.commands.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MythicDropCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("mythicdrop")) {
            // If no arguments or one argument, suggest "reload"
            if (args.length == 1) {
                List<String> subcommands = new ArrayList<>();
                subcommands.add("reload");
                return subcommands.stream()
                        .filter(subcommand -> subcommand.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

}
