# Commands & Permissions

This page lists every command available in MythicDrop, who can use them, and what they do.

---

## Color Codes

In messages and lore, use `&` followed by a letter/number for colors:

| Code | Color |
|---|---|
| `&a` | Green |
| `&b` | Aqua |
| `&c` | Red |
| `&e` | Yellow |
| `&f` | White |
| `&6` | Gold |
| `&d` | Light Purple |
| `&l` | **Bold** |
| `&k` | Obfuscated |

---

## `/mythicdrop reload`

**What it does:** Reloads all configuration files without restarting the server.

**Permission:** `mythicdrop.reload`
**Default:** OP only

**What gets reloaded:**
- `config.yml` (mob drops)
- `quests.yml` (quests)
- `top3damage.yml`, `top5damage.yml`, and any custom `topNdamage.yml`
- `announcement.yml`
- `effects.yml`
- `debug.yml`

**Example:**
```
/mythicdrop reload
```

> Always run this after changing any config file!

---

## `/marena`

**What it does:** Manages boss arenas — areas where a MythicMob automatically spawns and respawns.

**Permission:** `mythicdrop.arena`
**Default:** OP only

---

### `/marena setspawn <name> <mob> <respawn> <despawn> <radius>`

Creates (or updates) an arena at your current location.

| Argument | Description | Example |
|---|---|---|
| `name` | A unique name for this arena | `skeletonking_arena` |
| `mob` | The internal MythicMobs mob name | `SkeletonKing` |
| `respawn` | Seconds before mob respawns after death | `30` |
| `despawn` | Seconds before mob auto-despawns if not killed | `300` |
| `radius` | Arena radius (informational, used for reference) | `20` |

**Example:**
```
/marena setspawn skeletonking_arena SkeletonKing 30 300 20
```

This will:
1. Save the arena at your feet
2. Spawn the mob immediately
3. Set the mob to respawn 30 seconds after dying
4. Set the mob to despawn automatically after 300 seconds if not killed

---

### `/marena delete <name>`

Removes an arena and kills any mob currently spawned for it.

**Example:**
```
/marena delete skeletonking_arena
```

---

### `/marena list`

Shows all currently configured arenas with their details.

**Example output:**
```
--- Arenas ---
- skeletonking_arena | Mob: SkeletonKing | World: world | X: 100 Y: 64 Z: 200 | Respawn: 30s | Despawn: 300s | Radius: 20
```

---

## `/mquests`

**What it does:** Opens the Quest GUI for the player who runs it.

**Permission:** `mythicdrop.quests`
**Default:** All players

Players can:
- Browse all quests
- Filter by "In Progress" or "Completed"
- Click a quest to see their progress and available rewards

---

## `/mmobs`

**What it does:** Opens a GUI showing all MythicMobs configured on the server.

**Permission:** `mythicdrop.mmobs`
**Default:** OP only

Useful for admins to browse available mobs when setting up arenas or quests.

---

## Permission Summary

| Permission | Command | Default |
|---|---|---|
| `mythicdrop.reload` | `/mythicdrop reload` | OP |
| `mythicdrop.arena` | `/marena` | OP |
| `mythicdrop.quests` | `/mquests` | Everyone |
| `mythicdrop.mmobs` | `/mmobs` | OP |

To grant a permission to a non-OP player with LuckPerms:
```
/lp user <playername> permission set mythicdrop.quests true
```
