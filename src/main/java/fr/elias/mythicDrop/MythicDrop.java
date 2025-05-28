package fr.elias.mythicDrop;

import fr.elias.mythicDrop.commands.MythicDropCommand;
import fr.elias.mythicDrop.commands.tabCompleters.MythicDropCompleter;
import fr.elias.mythicDrop.listeners.MythicMobListener;
import fr.elias.mythicDrop.utils.Config;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import static fr.elias.mythicDrop.utils.DebugLogger.logDebug;


public class MythicDrop extends JavaPlugin {
    @Getter
    private static MythicDrop instance;
    @Getter
    private LuckPerms luckPerms;
    @Getter
    private final Set<UUID> processedMobEvents = new HashSet<>();
    @Getter
    private final Set<UUID> processedTop3Events = new HashSet<>();
    @Getter
    private final Set<UUID> processedTop5Events = new HashSet<>();
    @Getter
    private Config config;
    public static Config debugConfig;
    public static Config top3Config;
    public static Config top5Config;
    public static Config announcementConfig;

    @Override
    public void onEnable() {
        try {
            instance = this;

            // Save default Bukkit config if not exists
            saveDefaultConfig();

            // Ensure Bukkit's config is loaded (backing getConfig())
            reloadConfig(); // <- crucial to avoid NPE when calling getConfig()

            // Initialize debug config first to ensure debug logs can be used
            debugConfig = new Config("debug.yml");
            logDebug("Starting MythicDrop plugin initialization...");

            // Initialize custom wrapped config after Bukkit reloadConfig()
            this.config = new Config("config.yml");
            logDebug("Main configuration loaded.");

            // Load all additional configurations
            top3Config = new Config("top3damage.yml");
            top5Config = new Config("top5damage.yml");
            announcementConfig = new Config("announcement.yml");

            // Register listeners
            Bukkit.getPluginManager().registerEvents(new MythicMobListener(), this);
            logDebug("Event listeners registered.");

            // Register commands and tab completers
            if (this.getCommand("mythicdrop") != null) {
                Objects.requireNonNull(this.getCommand("mythicdrop")).setExecutor(new MythicDropCommand());
                Objects.requireNonNull(this.getCommand("mythicdrop")).setTabCompleter(new MythicDropCompleter());
                logDebug("Commands and tab completers registered.");
            } else {
                logDebug("Failed to register commands for 'mythicdrop'.");
            }

            // Validate MythicMobs dependency
            if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
                logDebug("MythicMobs found, proceeding...");

                try {
                    this.luckPerms = LuckPermsProvider.get();
                    logDebug("LuckPerms API initialized successfully.");
                } catch (IllegalStateException e) {
                    logDebug("LuckPerms API could not be initialized: " + e.getMessage());
                }

            } else {
                logDebug("MythicMobs is not installed. Disabling MythicDrop...");
                getServer().getPluginManager().disablePlugin(this);
            }

            logDebug("MythicDrop plugin enabled successfully.");
        } catch (Exception e) {
            getLogger().severe("An error occurred while enabling MythicDrop: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }




    @Override
    public void onDisable() {
        // Clean up resources if needed
    }

    /**
     * Get the primary group of the player using LuckPerms.
     *
     * @param player The player whose group to fetch.
     * @return The primary group name.
     */
    public String getPrimaryGroup(Player player) {

        // Check if LuckPerms API is available
        if (luckPerms == null) {
            logDebug("LuckPerms API is not initialized. Using default group for player: " + player.getName());
            return "default";
        }

        // Attempt to fetch the LuckPerms user
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            logDebug("LuckPerms user found for player: " + player.getName());

            // Attempt to fetch the primary group
            String primaryGroup = user.getPrimaryGroup();
            if (!primaryGroup.isEmpty()) {
                logDebug("Fetched primary group for player: " + player.getName() + " - " + primaryGroup);
                return primaryGroup;
            } else {
                logDebug("Primary group for player: " + player.getName() + " is null or empty. Using default group.");
            }
        } else {
            logDebug("No LuckPerms user found for player: " + player.getName() + ". Using default group.");
        }

        // Default fallback
        logDebug("Returning default group for player: " + player.getName());
        return "default";
    }

}