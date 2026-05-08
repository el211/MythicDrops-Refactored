package fr.elias.mythicDrop.commands.tabCompleters;

import fr.elias.mythicDrop.utils.ArenaManager;
import io.lumine.mythic.api.MythicProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MArenaCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("setspawn", "delete", "list");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }

        switch (args[0].toLowerCase()) {

            case "setspawn" -> {
                // /marena setspawn <name> <mob> <respawn> <despawn> <radius>
                if (args.length == 2) return List.of("<name>");
                if (args.length == 3) {
                    return filter(getMythicMobNames(), args[2]);
                }
                if (args.length == 4) return List.of("<respawn_seconds>");
                if (args.length == 5) return List.of("<despawn_seconds>");
                if (args.length == 6) return List.of("<radius>");
            }

            case "delete" -> {
                if (args.length == 2) {
                    Set<String> names = ArenaManager.getInstance().getArenaNames();
                    return filter(new ArrayList<>(names), args[1]);
                }
            }

            case "list" -> { /* no further args */ }
        }

        return List.of();
    }

    private List<String> filter(List<String> options, String partial) {
        String lower = partial.toLowerCase();
        return options.stream()
                .filter(o -> o.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }

    /** Fetches all MythicMob internal names via the public API interface. */
    private List<String> getMythicMobNames() {
        try {
            return new ArrayList<>(MythicProvider.get().getMobManager().getMobNames());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
