package fr.elias.mythicDrop.utils;

import fr.elias.mythicDrop.MythicDrop;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaManager {

    private static ArenaManager instance;

    private final MythicDrop plugin;
    private final File arenaFile;
    private YamlConfiguration arenaConfig;

    // mob UUID -> arena name (tracks currently live arena mobs)
    private final Map<UUID, String> liveMobs = new HashMap<>();
    // arena name -> pending respawn task ID
    private final Map<String, Integer> respawnTasks = new HashMap<>();
    // mob UUID -> pending despawn task ID
    private final Map<UUID, Integer> despawnTasks = new HashMap<>();

    private ArenaManager(MythicDrop plugin) {
        this.plugin = plugin;
        this.arenaFile = new File(plugin.getDataFolder(), "arenas.yml");
        load();
    }

    public static ArenaManager getInstance() {
        if (instance == null) {
            instance = new ArenaManager(MythicDrop.getInstance());
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    private void load() {
        if (!arenaFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                arenaFile.createNewFile();
            } catch (IOException e) {
                MythicLogger.severe("Failed to create arenas.yml: " + e.getMessage());
            }
        }
        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
    }

    private void save() {
        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            MythicLogger.severe("Failed to save arenas.yml: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    /**
     * Creates or updates an arena and immediately spawns the mob.
     */
    public void setArena(String name, Location loc, String mob, int respawnDelay, int despawnDelay, int radius) {
        String path = "arenas." + name;
        arenaConfig.set(path + ".mob", mob);
        arenaConfig.set(path + ".world", loc.getWorld().getName());
        arenaConfig.set(path + ".x", loc.getX());
        arenaConfig.set(path + ".y", loc.getY());
        arenaConfig.set(path + ".z", loc.getZ());
        arenaConfig.set(path + ".yaw", (double) loc.getYaw());
        arenaConfig.set(path + ".pitch", (double) loc.getPitch());
        arenaConfig.set(path + ".respawn", respawnDelay);
        arenaConfig.set(path + ".despawn", despawnDelay);
        arenaConfig.set(path + ".radius", radius);
        save();
        // Cancel any old tasks for this arena before spawning fresh
        cancelArenaTasks(name);
        spawnArena(name);
    }

    /**
     * Deletes an arena by name. Returns false if the arena does not exist.
     */
    public boolean deleteArena(String name) {
        if (!arenaConfig.contains("arenas." + name)) return false;
        cancelArenaTasks(name);
        arenaConfig.set("arenas." + name, null);
        save();
        return true;
    }

    /**
     * Returns all configured arena names.
     */
    public Set<String> getArenaNames() {
        if (!arenaConfig.isConfigurationSection("arenas")) return Collections.emptySet();
        return Objects.requireNonNull(arenaConfig.getConfigurationSection("arenas")).getKeys(false);
    }

    public boolean arenaExists(String name) {
        return arenaConfig.contains("arenas." + name);
    }

    /**
     * Returns a formatted summary line for the given arena.
     */
    public String getArenaInfo(String name) {
        String p = "arenas." + name + ".";
        return String.format(
            "&e%s &7— mob: &f%s&7, world: &f%s&7, xyz: &f%.1f/%.1f/%.1f&7, respawn: &f%ds&7, despawn: &f%ds&7, radius: &f%d",
            name,
            arenaConfig.getString(p + "mob", "?"),
            arenaConfig.getString(p + "world", "?"),
            arenaConfig.getDouble(p + "x"),
            arenaConfig.getDouble(p + "y"),
            arenaConfig.getDouble(p + "z"),
            arenaConfig.getInt(p + "respawn"),
            arenaConfig.getInt(p + "despawn"),
            arenaConfig.getInt(p + "radius")
        );
    }

    // -------------------------------------------------------------------------
    // Spawning
    // -------------------------------------------------------------------------

    /**
     * Spawns all configured arenas (called on plugin enable).
     */
    public void spawnAll() {
        for (String name : getArenaNames()) {
            spawnArena(name);
        }
    }

    /**
     * Spawns the mob for the given arena.
     */
    public void spawnArena(String name) {
        String p = "arenas." + name + ".";
        String mobType = arenaConfig.getString(p + "mob");
        String worldName = arenaConfig.getString(p + "world");
        if (mobType == null || worldName == null) {
            MythicLogger.warn("Arena '" + name + "' has missing mob or world — skipping spawn.");
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            MythicLogger.warn("Arena '" + name + "': world '" + worldName + "' is not loaded — skipping spawn.");
            return;
        }

        Optional<MythicMob> mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(mobType);
        if (mythicMob.isEmpty()) {
            MythicLogger.warn("Arena '" + name + "': MythicMob '" + mobType + "' does not exist — skipping spawn.");
            return;
        }

        double x = arenaConfig.getDouble(p + "x");
        double y = arenaConfig.getDouble(p + "y");
        double z = arenaConfig.getDouble(p + "z");
        float yaw = (float) arenaConfig.getDouble(p + "yaw");
        float pitch = (float) arenaConfig.getDouble(p + "pitch");

        Location loc = new Location(world, x, y, z, yaw, pitch);
        ActiveMob activeMob = mythicMob.get().spawn(BukkitAdapter.adapt(loc), 1);

        if (activeMob == null) {
            MythicLogger.warn("Arena '" + name + "': spawn call returned null for mob '" + mobType + "'.");
            return;
        }

        UUID mobUUID = activeMob.getUniqueId();
        liveMobs.put(mobUUID, name);

        // Schedule despawn if configured
        int despawnDelay = arenaConfig.getInt(p + "despawn");
        if (despawnDelay > 0) {
            int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                despawnTasks.remove(mobUUID);
                if (liveMobs.remove(mobUUID) != null) {
                    activeMob.despawn();
                    scheduleRespawn(name);
                }
            }, (long) despawnDelay * 20L).getTaskId();
            despawnTasks.put(mobUUID, taskId);
        }
    }

    // -------------------------------------------------------------------------
    // Death / Despawn hooks (called from MythicMobListener)
    // -------------------------------------------------------------------------

    /**
     * Called when any MythicMob dies or despawns.
     * If it belonged to an arena, a respawn is scheduled.
     */
    public void handleMobRemoved(UUID mobUUID) {
        String arenaName = liveMobs.remove(mobUUID);
        if (arenaName == null) return;

        // Cancel pending despawn task for this mob
        Integer despawnTask = despawnTasks.remove(mobUUID);
        if (despawnTask != null) Bukkit.getScheduler().cancelTask(despawnTask);

        scheduleRespawn(arenaName);
    }

    private void scheduleRespawn(String name) {
        if (!arenaConfig.contains("arenas." + name)) return;
        // Don't schedule a second respawn if one is already queued
        if (respawnTasks.containsKey(name)) return;

        int delay = arenaConfig.getInt("arenas." + name + ".respawn");
        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            respawnTasks.remove(name);
            if (arenaConfig.contains("arenas." + name)) {
                spawnArena(name);
            }
        }, (long) delay * 20L).getTaskId();
        respawnTasks.put(name, taskId);
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    private void cancelArenaTasks(String name) {
        Integer rt = respawnTasks.remove(name);
        if (rt != null) Bukkit.getScheduler().cancelTask(rt);

        liveMobs.entrySet().removeIf(entry -> {
            if (name.equals(entry.getValue())) {
                Integer dt = despawnTasks.remove(entry.getKey());
                if (dt != null) Bukkit.getScheduler().cancelTask(dt);
                return true;
            }
            return false;
        });
    }

    /**
     * Called on plugin disable — cancels all pending tasks.
     */
    public void shutdown() {
        respawnTasks.values().forEach(Bukkit.getScheduler()::cancelTask);
        despawnTasks.values().forEach(Bukkit.getScheduler()::cancelTask);
        respawnTasks.clear();
        despawnTasks.clear();
        liveMobs.clear();
    }
}
