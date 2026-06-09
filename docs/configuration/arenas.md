# Arena System

Arenas are locations where a MythicMob **automatically spawns** and **respawns** after being killed. This is great for boss rooms that should always have an active boss.

---

## File Location

Arenas are managed through in-game commands, not by editing a file directly. The plugin saves them automatically to:

```
plugins/MythicDropRefactored/arenas.yml
```

You do not need to edit `arenas.yml` manually.

---

## How Arenas Work

1. You stand at the location where you want the boss to spawn
2. You run `/marena setspawn` with the mob name and timing settings
3. The boss spawns immediately at your location
4. When the boss dies, it will respawn after the delay you set
5. If the boss hasn't been killed after the despawn timer, it disappears and then respawns

---

## Creating an Arena

Stand at the exact spawn point, then run:

```
/marena setspawn <name> <mob> <respawn> <despawn> <radius>
```

| Argument | What it means | Example |
|---|---|---|
| `name` | A unique name for your arena. No spaces — use `_` instead. | `skeleton_boss_room` |
| `mob` | The internal MythicMobs mob name (must match exactly, case-sensitive) | `SkeletonKing` |
| `respawn` | How many seconds before the mob respawns after being killed | `60` |
| `despawn` | How many seconds before the mob auto-despawns if no one kills it (`0` = never) | `600` |
| `radius` | The arena size (informational only, not enforced) | `15` |

### Example

You're standing in a boss room and want to spawn a Skeleton King that:
- Respawns 60 seconds after being killed
- Auto-despawns after 10 minutes if no one kills it
- Has a radius of 15 blocks

```
/marena setspawn skeleton_boss_room SkeletonKing 60 600 15
```

The boss will spawn at your feet immediately.

---

## Listing Arenas

```
/marena list
```

Shows all configured arenas and their details:

```
--- Arenas ---
- skeleton_boss_room | Mob: SkeletonKing | World: world | X: 105 Y: 64 Z: -220 | Respawn: 60s | Despawn: 600s | Radius: 15
- dragon_lair | Mob: AncientDragon | World: world_nether | X: 0 Y: 100 Z: 0 | Respawn: 120s | Despawn: 900s | Radius: 30
```

---

## Deleting an Arena

```
/marena delete <name>
```

This will:
1. Remove the arena from the config
2. Kill any currently spawned mob for that arena

**Example:**
```
/marena delete skeleton_boss_room
```

---

## Arena Respawn Timing

Here's how the timing works in practice:

```
Boss spawns
    |
    | (players fight)
    |
Boss dies  -----> Respawn timer starts (e.g. 60 seconds)
                        |
                        | (60 seconds pass)
                        |
                    Boss spawns again
```

If `despawn` is set:

```
Boss spawns
    |
    | (no players fight, timer runs)
    |
Despawn timer expires (e.g. 600s) -----> Boss despawns
                                              |
                                              | Respawn timer then starts
                                              |
                                          Boss spawns again
```

---

## Tips

- **Respawn = 0**: The boss respawns instantly after dying (not recommended — can cause lag)
- **Despawn = 0**: The boss never auto-despawns (it stays until killed)
- **Best practice**: Set respawn to at least `30` seconds so players have time to collect drops before the next boss appears
- **Multiple arenas**: You can have as many arenas as you want with different mobs
- **Arena names**: Use descriptive names like `boss_room_1`, `skeleton_king_arena`, `nether_fortress_boss`

---

## Arenas and Rewards

Arenas work with all reward systems:
- If the arena mob is in `top3damage.yml`, it uses the Top-3 reward system
- If it's in `config.yml`, it uses standard drops
- Quests tracking that mob type will count kills from arena mobs too

---

## What Gets Saved

When you create an arena, `arenas.yml` is updated automatically:

```yaml
arenas:
  skeleton_boss_room:
    mob: SkeletonKing
    world: world
    x: 105.5
    y: 64.0
    z: -220.5
    yaw: 0.0
    pitch: 0.0
    respawn: 60
    despawn: 600
    radius: 15
```

You generally should not edit this file manually — use the commands instead.

---

## PlaceholderAPI Integration

If you have PlaceholderAPI installed, you can show arena info on scoreboards or signs:

| Placeholder | What it shows |
|---|---|
| `%mythicdrop_arena_count%` | How many arenas exist |
| `%mythicdrop_arena_live_count%` | How many arenas currently have a live boss |
| `%mythicdrop_arena_<name>_exists%` | `true` or `false` — does this arena exist? |
| `%mythicdrop_arena_<name>_mob%` | The MythicMob type of this arena |
| `%mythicdrop_arena_<name>_world%` | The world name of this arena |
| `%mythicdrop_arena_<name>_alive%` | `true` or `false` — is the boss currently alive? |

Replace `<name>` with your actual arena name (e.g., `%mythicdrop_arena_skeleton_boss_room_alive%`).
