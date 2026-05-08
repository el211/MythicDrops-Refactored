package fr.elias.mythicDrop;

import fr.elias.mythicDrop.commands.MArenaCommand;
import fr.elias.mythicDrop.commands.MMobsCommand;
import fr.elias.mythicDrop.commands.MQuestsCommand;
import fr.elias.mythicDrop.commands.MythicDropCommand;
import fr.elias.mythicDrop.commands.tabCompleters.MArenaCompleter;
import fr.elias.mythicDrop.commands.tabCompleters.MythicDropCompleter;
import fr.elias.mythicDrop.effects.EffectInitializer;
import fr.elias.mythicDrop.gui.MobsGUI;
import fr.elias.mythicDrop.gui.QuestsSmartGUI;
import fr.elias.mythicDrop.listeners.MythicMobListener;
import fr.elias.mythicDrop.listeners.QuestCompletionListener;
import fr.elias.mythicDrop.listeners.QuestGUIListener;
import fr.elias.mythicDrop.listeners.QuestMobKillListener;
import fr.elias.mythicDrop.quests.QuestDataPersistence;
import fr.elias.mythicDrop.quests.QuestManager;
import fr.elias.mythicDrop.utils.ArenaManager;
import fr.elias.mythicDrop.utils.Config;
import fr.elias.mythicDrop.utils.DamageTracker;
import fr.elias.mythicDrop.utils.TopXManager;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
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
    public Config config;
    public static Config debugConfig;
    public static Config top3Config;
    public static Config top5Config;
    public static Config announcementConfig;
    public static Config effectsConfig;
    public static Config questsConfig;

    @Getter
    private QuestManager questManager;
    private QuestDataPersistence questDataPersistence;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private TopXManager topXManager;
    private QuestsSmartGUI questsSmartGUI;

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
            EffectInitializer.registerDefaults();

            // Load all additional configurations
            top3Config = new Config("top3damage.yml");
            top5Config = new Config("top5damage.yml");
            announcementConfig = new Config("announcement.yml");
            effectsConfig = new Config("effects.yml");
            questsConfig = new Config("quests.yml");

            // Initialize SmartInvs
            inventoryManager = new InventoryManager(this);
            inventoryManager.init();
            logDebug("SmartInvs initialized.");

            // Initialize TopX manager (auto-discovers topNdamage.yml files)
            topXManager = new TopXManager(getDataFolder());
            logDebug("TopX manager loaded " + topXManager.getConfigs().size() + " custom topX config(s).");

            // Initialize quest system
            questManager = new QuestManager(this);
            questManager.loadQuests(questsConfig);
            questDataPersistence = new QuestDataPersistence(this, questManager);
            questsSmartGUI = new QuestsSmartGUI(inventoryManager, questManager);
            logDebug("Quest system initialized.");

            // Register listeners
            Bukkit.getPluginManager().registerEvents(new MythicMobListener(), this);
            Bukkit.getPluginManager().registerEvents(new QuestMobKillListener(questManager), this);
            Bukkit.getPluginManager().registerEvents(new QuestCompletionListener(), this);
            Bukkit.getPluginManager().registerEvents(new QuestGUIListener(), this);
            logDebug("Event listeners registered.");

            // Register commands and tab completers
            if (this.getCommand("mythicdrop") != null) {
                Objects.requireNonNull(this.getCommand("mythicdrop")).setExecutor(new MythicDropCommand());
                Objects.requireNonNull(this.getCommand("mythicdrop")).setTabCompleter(new MythicDropCompleter());
                logDebug("Commands and tab completers registered.");
            } else {
                logDebug("Failed to register commands for 'mythicdrop'.");
            }

            if (this.getCommand("marena") != null) {
                Objects.requireNonNull(this.getCommand("marena")).setExecutor(new MArenaCommand());
                Objects.requireNonNull(this.getCommand("marena")).setTabCompleter(new MArenaCompleter());
                logDebug("MArena commands registered.");
            } else {
                logDebug("Failed to register commands for 'marena'.");
            }

            if (this.getCommand("mquests") != null) {
                Objects.requireNonNull(this.getCommand("mquests")).setExecutor(new MQuestsCommand(questsSmartGUI));
                logDebug("MQuests command registered.");
            } else {
                logDebug("Failed to register commands for 'mquests'.");
            }

            if (this.getCommand("mmobs") != null) {
                MobsGUI mobsGUI = new MobsGUI(inventoryManager);
                Objects.requireNonNull(this.getCommand("mmobs")).setExecutor(new MMobsCommand(mobsGUI));
                logDebug("MMobs command registered.");
            } else {
                logDebug("Failed to register commands for 'mmobs'.");
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

                // Initialize arena manager and spawn all configured arenas
                ArenaManager.getInstance().spawnAll();
                logDebug("Arena manager initialized and arenas spawned.");

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




    public QuestsSmartGUI getQuestsSmartGUI() {
        return questsSmartGUI;
    }

    @Override
    public void onDisable() {
        if (questDataPersistence != null) questDataPersistence.save();
        if (topXManager != null) topXManager.reload(); // clear caches
        ArenaManager.getInstance().shutdown();
        processedMobEvents.clear();
        processedTop3Events.clear();
        processedTop5Events.clear();
        DamageTracker.clearAll();
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

        try {
            PlayerAdapter<Player> playerAdapter = luckPerms.getPlayerAdapter(Player.class);
            String primaryGroup = playerAdapter.getMetaData(player).getPrimaryGroup();
            if (primaryGroup != null && !primaryGroup.isEmpty()) {
                logDebug("Fetched primary group from LuckPerms metadata for player: " + player.getName() + " - " + primaryGroup);
                return primaryGroup;
            }

            User liveUser = playerAdapter.getUser(player);
            if (liveUser != null && !liveUser.getPrimaryGroup().isEmpty()) {
                logDebug("Fetched primary group from LuckPerms live user for player: " + player.getName() + " - " + liveUser.getPrimaryGroup());
                return liveUser.getPrimaryGroup();
            }
        } catch (Exception e) {
            logDebug("Failed to fetch LuckPerms metadata for player: " + player.getName() + " - " + e.getMessage());
        }

        User cachedUser = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (cachedUser != null && !cachedUser.getPrimaryGroup().isEmpty()) {
            logDebug("Fetched primary group from LuckPerms cached user for player: " + player.getName() + " - " + cachedUser.getPrimaryGroup());
            return cachedUser.getPrimaryGroup();
        }

        // Default fallback
        logDebug("Returning default group for player: " + player.getName());
        return "default";
    }

}
