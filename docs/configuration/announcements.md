# Damage Announcements (`announcement.yml`)

When a boss dies, MythicDrop can **announce to all players** on the server who dealt the most damage — like a mini leaderboard in chat.

---

## File Location

```
plugins/MythicDropRefactored/announcement.yml
```

---

## Basic Structure

```yaml
announce-on-death: true

messages:
  header: "&aLIST OF PLAYERS WHO HAVE INFLICTED THE MOST DAMAGE ON %BOSSNAME%:"
  entry: "&e%position%. %player% - %damage% DAMAGE"
  no-players: "&cNo players contributed damage to the mob."

announce-specific-mob:
  SkeletonKing: true
  CaveSpider: false
```

---

## Field Explanations

### `announce-on-death`

```yaml
announce-on-death: true
```

| Value | Effect |
|---|---|
| `true` | Announce the damage ranking when any mob dies (unless overridden per-mob) |
| `false` | Don't announce by default (can still enable for specific mobs) |

---

### `messages`

Controls how the announcement looks in chat.

```yaml
messages:
  header: "&aLIST OF PLAYERS WHO HAVE INFLICTED THE MOST DAMAGE ON %BOSSNAME%:"
  entry: "&e%position%. %player% - %damage% DAMAGE"
  no-players: "&cNo players contributed damage to the mob."
```

| Field | Description |
|---|---|
| `header` | The first line of the announcement — shown once |
| `entry` | Repeated once per player in the ranking |
| `no-players` | Shown if no players dealt damage to the mob |

#### Variables

Use these in your `header` and `entry` messages:

| Variable | What it becomes |
|---|---|
| `%BOSSNAME%` | The MythicMobs internal name of the mob |
| `%position%` | The player's rank (1, 2, 3, etc.) |
| `%player%` | The player's name |
| `%damage%` | The total damage the player dealt (whole number) |

---

### `announce-specific-mob`

Override the global `announce-on-death` setting for specific mobs.

```yaml
announce-specific-mob:
  SkeletonKing: true     # Always announce, even if global is false
  CaveSpider: false      # Never announce, even if global is true
  AncientDragon: true
```

- If a mob is **not listed** here, the global `announce-on-death` setting applies
- If a mob **is listed**, its value overrides the global setting

---

## How Many Players Are Shown?

The announcement shows a different number of players depending on which reward system the mob uses:

| Reward System | Players Shown in Announcement |
|---|---|
| Top-3 mob | Top 3 players |
| Top-5 mob | Top 5 players |
| Custom TopN mob | Top N players |
| Standard drops (config.yml) | All players who dealt damage |

---

## Example: Full Chat Announcement

With the default settings, when `SkeletonKing` dies, all players see:

```
LIST OF PLAYERS WHO HAVE INFLICTED THE MOST DAMAGE ON SkeletonKing:
1. Steve - 1250 DAMAGE
2. Alex - 980 DAMAGE
3. Notch - 520 DAMAGE
```

---

## Full Example Configuration

```yaml
announce-on-death: true

messages:
  header: "&6&l--- &eBoss Defeated: &f%BOSSNAME% &6&l---"
  entry: "  &e#%position% &f%player% &7— &c%damage% &7damage dealt"
  no-players: "&cNo one dealt damage to this mob."

announce-specific-mob:
  SkeletonKing: true       # Always announce
  AncientDragon: true      # Always announce
  TrainingDummy: false     # Never announce (it's just a training mob)
  CaveSpider: false        # Spiders are too common to spam chat
```

With this config, when SkeletonKing dies:

```
--- Boss Defeated: SkeletonKing ---
  #1 Steve — 1250 damage dealt
  #2 Alex — 980 damage dealt
  #3 Notch — 520 damage dealt
```

---

## Tips

- Use `&l` (bold) and `&6` (gold) in the header to make it stand out in chat
- Keep the `entry` format short — multiple players will each get a line
- Disable announcements for common/trash mobs with `announce-specific-mob` to avoid chat spam
- The announcement is broadcast to **all online players** — not just those who participated

---

## After Editing

Run `/mythicdrop reload` to apply your changes.
