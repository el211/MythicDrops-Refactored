# Quest System (`quests.yml`)

The quest system lets players track kill goals and earn rewards when they complete them. Players can view their quests with the `/mquests` command.

---

## File Location

```
plugins/MythicDropRefactored/quests.yml
```

---

## How Quests Work (Overview)

1. You define a quest — target mob, how many kills needed, and what rewards to give
2. Players open `/mquests` to see their quests in a GUI
3. When a player kills the target MythicMob, their progress goes up by 1
4. When they hit the required number of kills, the quest is marked **completed**
5. Rewards (commands) run automatically
6. Progress is saved permanently — even after relogging or restarting the server

---

## Basic Structure

```yaml
quests:
  my_first_quest:               # Unique quest ID — no spaces, use _ instead
    mob-name: "SkeletonKing"    # MythicMobs internal mob name
    message: "&aQuest complete! You slayed the Skeleton King!"

    quest-item:
      material: SKELETON_SKULL  # Item shown in the GUI
      ModelData: 0
      displayname: "&fSkeleton King Slayer"
      lore:
        - "&7Defeat the Skeleton King."
        - "&7Required kills: &f5"

    conditions:
      kill: 5                   # Number of kills needed
      slot: 10                  # GUI slot (see slot layout below)

    reward:
      drops:
        default:
          guaranteed-rewards: 1
          reward1:
            command: "give %player% diamond 5"
            chance: 1.0
            message: "&b+5 Diamonds!"
```

---

## Quest Fields Explained

### Top-Level Fields

| Field | Description |
|---|---|
| `mob-name` | The internal MythicMobs name of the mob to kill. Must match exactly (case-sensitive). |
| `message` | The chat message sent to the player when they complete the quest. Supports `&` color codes. |

---

### `quest-item` — How the Quest Looks in the GUI

```yaml
quest-item:
  material: SPIDER_EYE       # Any Minecraft material name (uppercase)
  ModelData: 0               # Custom model data for resource packs (0 = default)
  displayname: "&2Cave Spider Slayer"
  lore:
    - "&7Defeat cave spiders."
    - "&7Required kills: &f10"
```

| Field | Description |
|---|---|
| `material` | The Minecraft item displayed in the GUI (e.g., `DIAMOND_SWORD`, `SKULL`, `PAPER`) |
| `ModelData` | Custom model data number for resource packs. Use `0` if unsure. |
| `displayname` | The name shown on the item in the GUI. Supports color codes. |
| `lore` | A list of description lines. Each line is indented with `- `. Supports color codes. |

---

### `conditions` — Quest Requirements

```yaml
conditions:
  kill: 10    # Must kill this mob 10 times
  slot: 11    # Position in the GUI grid (0-44)
```

#### `kill`
The number of times the player must kill the target mob.

#### `slot` — GUI Grid Layout

The quest GUI has 5 rows of 9 slots (slots 0-44). The bottom row is reserved for filter buttons.

```
Slot positions (row × column):
+----+----+----+----+----+----+----+----+----+
|  0 |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  Row 1
+----+----+----+----+----+----+----+----+----+
|  9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 |  Row 2
+----+----+----+----+----+----+----+----+----+
| 18 | 19 | 20 | 21 | 22 | 23 | 24 | 25 | 26 |  Row 3
+----+----+----+----+----+----+----+----+----+
| 27 | 28 | 29 | 30 | 31 | 32 | 33 | 34 | 35 |  Row 4
+----+----+----+----+----+----+----+----+----+
| 36 | 37 | 38 | 39 | 40 | 41 | 42 | 43 | 44 |  Row 5
+----+----+----+----+----+----+----+----+----+
| [All Quests] |    |  [In Progress]  |    | [Completed] |  Bottom row (reserved)
```

Place quests in slots 0-44. Do **not** use the bottom row (slots 45-53) — those are taken by the filter buttons.

---

### `reward` — What Players Receive

The reward section uses the same format as `config.yml` drops, but with an added `guaranteed-rewards` option.

```yaml
reward:
  drops:
    default:                      # LuckPerms group (use "default" for everyone)
      guaranteed-rewards: 1       # First N rewards ALWAYS run (no chance roll)
      reward1:
        command: "give %player% diamond 5"
        chance: 1.0
        message: "&b+5 Diamonds!"
      reward2:
        command: "give %player% emerald 3"
        chance: 0.5               # 50% chance — only rolls if not in guaranteed range
        message: "&a+3 Emeralds!"
    
    vip:                          # Different rewards for VIP players
      guaranteed-rewards: 2       # VIPs always get both reward1 and reward2
      reward1:
        command: "give %player% diamond 10"
        chance: 1.0
        message: "&b+10 Diamonds!"
      reward2:
        command: "give %player% emerald 5"
        chance: 1.0
        message: "&a+5 Emeralds!"
```

#### `guaranteed-rewards`

The number of rewards (counted from the top of the list) that are **always given**, regardless of their `chance` value.

**Example:**
- `guaranteed-rewards: 1` → `reward1` always runs, `reward2` and beyond roll their chance
- `guaranteed-rewards: 2` → `reward1` and `reward2` always run, rest roll their chance
- `guaranteed-rewards: 0` → all rewards roll their individual chances

---

## Full Example with Multiple Quests

```yaml
quests:

  # Quest 1: Kill 10 Cave Spiders
  cave_spider_slayer:
    mob-name: "CaveSpider"
    message: "&aQuest Complete! &7You have defeated 10 cave spiders!"

    quest-item:
      material: SPIDER_EYE
      ModelData: 0
      displayname: "&cCave Spider Slayer"
      lore:
        - "&7Defeat cave spiders in the mines."
        - "&7Required: &f10 kills"

    conditions:
      kill: 10
      slot: 10      # Second slot in second row

    reward:
      drops:
        default:
          guaranteed-rewards: 1
          base_reward:
            command: "give %player% string 8"
            chance: 1.0
            message: "&7+8 String"
          bonus_reward:
            command: "give %player% spider_eye 2"
            chance: 0.4
            message: "&cBonus! +2 Spider Eyes"

        vip:
          guaranteed-rewards: 2
          base_reward:
            command: "give %player% string 16"
            chance: 1.0
            message: "&7[VIP] +16 String"
          bonus_reward:
            command: "give %player% spider_eye 5"
            chance: 1.0
            message: "&c[VIP] +5 Spider Eyes"

  # Quest 2: Kill the Skeleton King 5 times
  skeleton_king_slayer:
    mob-name: "SkeletonKing"
    message: "&6Quest Complete! &eYou are a true Skeleton King Slayer!"

    quest-item:
      material: SKELETON_SKULL
      ModelData: 0
      displayname: "&fSkeleton King Slayer"
      lore:
        - "&7Defeat the mighty Skeleton King."
        - "&7Required: &f5 kills"
        - ""
        - "&7This is a hard quest!"

    conditions:
      kill: 5
      slot: 11      # Third slot in second row

    reward:
      drops:
        default:
          guaranteed-rewards: 1
          money:
            command: "eco give %player% 1000"
            chance: 1.0
            message: "&a+$1,000!"
          diamond_reward:
            command: "give %player% diamond 10"
            chance: 0.5
            message: "&b50% bonus: +10 Diamonds!"

        vip:
          guaranteed-rewards: 2
          money:
            command: "eco give %player% 2500"
            chance: 1.0
            message: "&a[VIP] +$2,500!"
          diamond_reward:
            command: "give %player% diamond 25"
            chance: 1.0
            message: "&b[VIP] +25 Diamonds!"
          legend_crate:
            command: "crate key give %player% legendary 1"
            chance: 0.3
            message: "&6VIP Bonus! Legendary Crate Key!"
```

---

## Quest GUI Overview

When players run `/mquests`, they see a GUI with:

- **All quest items** in the grid (based on their `slot` values)
- A **progress indicator** on each item (e.g., `5 / 10 kills`)
- A checkmark or color change when a quest is **completed**

### Filter Buttons (Bottom Row)

| Button | Description |
|---|---|
| All Quests (slot 46) | Shows every quest |
| In Progress (slot 49) | Shows only quests the player hasn't finished |
| Completed (slot 52) | Shows only finished quests |

### Clicking a Quest

Clicking any quest opens a **detail view** showing:
- The quest icon
- Current progress bar
- All rewards the player can earn (based on their group)

---

## How to Apply Changes

After editing `quests.yml`, reload with:
```
/mythicdrop reload
```

---

## Common Mistakes

| Problem | Solution |
|---|---|
| Quest not tracking kills | Check that `mob-name` exactly matches the MythicMobs internal mob name |
| Quest item not showing in GUI | Check the `slot` number — make sure it's between 0 and 44 |
| All players getting same rewards | Make sure you have the right LuckPerms group names in the `drops` section |
| Quest progress lost after restart | This shouldn't happen — check if `quest_data.yml` is being written correctly |
| Color codes showing as `&a` | Make sure you're using `&` not `§` |
