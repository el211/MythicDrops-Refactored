# Top Damage Reward System

The Top Damage system rewards players based on **how much damage they dealt** to a boss before it died. This is perfect for boss fights with multiple players — the best damage dealers get the best rewards.

---

## Overview

There are three built-in reward tiers:

| File | System | Description |
|---|---|---|
| `top3damage.yml` | Top 3 | Top 3 damage dealers + everyone else |
| `top5damage.yml` | Top 5 | Top 5 damage dealers + everyone else |
| `topNdamage.yml` | Custom | You can make any number (top7, top10, etc.) |

All three work the same way — the only difference is how many ranked positions there are.

---

## How It Works

1. A player (or multiple players) attacks a boss
2. The plugin tracks how much damage each player deals
3. When the boss dies, players are ranked by total damage
4. Top N players get their rank-specific rewards
5. Anyone outside the top N who dealt enough damage gets the "everyone else" reward

---

## File: `top3damage.yml`

```
plugins/MythicDropRefactored/top3damage.yml
```

### Basic Structure

```yaml
rewardtop3:
  - SkeletonKing          # List of mobs that use the top-3 system

rewardtop3-settings:
  use-flexible-rewards: false   # true = all rewards roll; false = stop at guaranteed count

SkeletonKing:
  use-standard-rewards: false   # true = also give rewards from config.yml
  guaranteedperrank: true
  per-rank-group: true
  guaranteed-rewards:
    default: 1
    vip: 2

  first-place:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 10"
        chance: 1.0
        message: "&b&l[#1] You dealt the most damage!"
    vip:
      drop1:
        command: "give %player% diamond 20"
        chance: 1.0
        message: "&b&l[VIP #1] Champion Reward!"

  second-place:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 5"
        chance: 1.0
        message: "&e[#2] Silver reward!"
    vip:
      drop1:
        command: "give %player% diamond 10"
        chance: 1.0
        message: "&e[VIP #2] Great job!"

  third-place:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 2"
        chance: 1.0
        message: "&c[#3] Bronze reward!"
    vip:
      drop1:
        command: "give %player% diamond 5"
        chance: 1.0
        message: "&c[VIP #3] Keep it up!"

  everyone-else-who-contributed:
    min-damage: 50.0          # Players must deal at least 50 damage to qualify
    default:
      drop1:
        command: "give %player% emerald 1"
        chance: 0.8
        message: "&aThanks for helping!"
    vip:
      drop1:
        command: "give %player% emerald 2"
        chance: 0.9
        message: "&a[VIP] Thanks for helping!"
```

---

## Field-by-Field Explanation

### `rewardtopN` list

```yaml
rewardtop3:
  - SkeletonKing
  - AncientDragon
```

List the **MythicMobs internal names** of bosses that should use the top-3 system. Each mob listed here will have its damage tracked and ranked.

---

### `rewardtopN-settings`

```yaml
rewardtop3-settings:
  use-flexible-rewards: false
```

| Setting | Value | Meaning |
|---|---|---|
| `use-flexible-rewards` | `false` | Only give guaranteed rewards, stop there |
| `use-flexible-rewards` | `true` | All rewards roll their individual chances |

---

### Mob Section

```yaml
SkeletonKing:
  use-standard-rewards: false  # Don't also run config.yml drops
  guaranteedperrank: true       # Each rank has its own guaranteed-rewards count
  per-rank-group: true          # Each group within a rank can have different guarantees
  guaranteed-rewards:           # Fallback if a rank doesn't set its own
    default: 1
    vip: 2
```

| Field | Description |
|---|---|
| `use-standard-rewards` | If `true`, also runs the drops from `config.yml` in addition to top rewards |
| `guaranteedperrank` | If `true`, each rank section can define its own `guaranteed-rewards` |
| `per-rank-group` | If `true`, `guaranteed-rewards` can be different per group within a rank |
| `guaranteed-rewards` | The fallback guaranteed count (used when a rank doesn't define its own) |

---

### Rank Sections

```yaml
first-place:
  guaranteed-rewards: 1     # Overrides the mob-level guaranteed-rewards
  default:
    drop1:
      command: "..."
      chance: 1.0
      message: "..."
  vip:
    drop1:
      command: "..."
      chance: 1.0
      message: "..."
```

Available ranks for **top3**:
- `first-place`
- `second-place`
- `third-place`

Available ranks for **top5**:
- `first-place`
- `second-place`
- `third-place`
- `fourth-place`
- `fifth-place`

---

### `everyone-else-who-contributed`

Players who aren't in the top N can still get rewards if they dealt enough damage.

```yaml
everyone-else-who-contributed:
  min-damage: 50.0      # Minimum damage required to qualify
  default:
    drop1:
      command: "give %player% emerald 1"
      chance: 0.8
      message: "&aThanks for contributing!"
  vip:
    drop1:
      command: "give %player% emerald 2"
      chance: 0.9
      message: "&a[VIP] Thanks for contributing!"
```

| Field | Description |
|---|---|
| `min-damage` | The minimum total damage a player must have dealt to qualify. Players below this threshold get nothing. |

---

## Custom TopX Files

You can create files for **any number** of ranked positions. Just create a file named `topNdamage.yml` (replace N with a number).

**Example: `top7damage.yml`**

```yaml
rewardtop7:
  - WorldBoss

rewardtop7-settings:
  use-flexible-rewards: true

WorldBoss:
  use-standard-rewards: false
  guaranteedperrank: true
  per-rank-group: false
  guaranteed-rewards: 1

  position-1:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% netherite_ingot 3"
        chance: 1.0
        message: "&4&l[#1] Netherite Champion!"

  position-2:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% netherite_ingot 2"
        chance: 1.0
        message: "&c[#2] Great job!"

  position-3:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% netherite_ingot 1"
        chance: 1.0
        message: "&6[#3] Not bad!"

  position-4:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 5"
        chance: 1.0
        message: "&b[#4] Good effort!"

  position-5:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 3"
        chance: 1.0
        message: "&b[#5] Keep improving!"

  position-6:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 2"
        chance: 1.0

  position-7:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 1"
        chance: 1.0

  everyone-else-who-contributed:
    min-damage: 100.0
    default:
      drop1:
        command: "give %player% emerald 1"
        chance: 1.0
        message: "&aThanks for fighting!"
```

> **Note:** For custom files, use `position-1`, `position-2`, etc. (not `first-place`, `second-place`).
> For `top3damage.yml` and `top5damage.yml`, use the named positions (`first-place`, `second-place`, etc.).

---

## Priority: Which System Runs?

When a mob dies, MythicDrop checks in this order:

1. Is the mob in `rewardtop3`? → Use Top-3 system
2. Is the mob in `rewardtop5`? → Use Top-5 system
3. Is the mob in any `topNdamage.yml`? → Use that TopX system
4. Does the mob have drops in `config.yml`? → Use standard drops

A mob can only be in **one** top-damage system at a time.

---

## Full Example (`top3damage.yml`)

```yaml
rewardtop3:
  - SkeletonKing
  - AncientDragon

rewardtop3-settings:
  use-flexible-rewards: false

# ---------- Skeleton King ----------
SkeletonKing:
  use-standard-rewards: false
  guaranteedperrank: true
  per-rank-group: true
  guaranteed-rewards:
    default: 1
    vip: 2
    legend: 3

  first-place:
    guaranteed-rewards: 1
    default:
      money:
        command: "eco give %player% 5000"
        chance: 1.0
        message: "&a+$5,000 (First Place!)"
      diamond_reward:
        command: "give %player% diamond 10"
        chance: 0.7
        message: "&b+10 Diamonds"
    vip:
      money:
        command: "eco give %player% 10000"
        chance: 1.0
        message: "&a[VIP] +$10,000 (First Place!)"
      diamond_reward:
        command: "give %player% diamond 20"
        chance: 0.8
        message: "&b+20 Diamonds"

  second-place:
    guaranteed-rewards: 1
    default:
      money:
        command: "eco give %player% 3000"
        chance: 1.0
        message: "&a+$3,000 (Second Place)"
    vip:
      money:
        command: "eco give %player% 6000"
        chance: 1.0
        message: "&a[VIP] +$6,000 (Second Place)"

  third-place:
    guaranteed-rewards: 1
    default:
      money:
        command: "eco give %player% 1000"
        chance: 1.0
        message: "&a+$1,000 (Third Place)"
    vip:
      money:
        command: "eco give %player% 2000"
        chance: 1.0
        message: "&a[VIP] +$2,000 (Third Place)"

  everyone-else-who-contributed:
    min-damage: 50.0
    default:
      money:
        command: "eco give %player% 200"
        chance: 1.0
        message: "&aThanks for helping! +$200"
    vip:
      money:
        command: "eco give %player% 400"
        chance: 1.0
        message: "&a[VIP] Thanks for helping! +$400"
```

---

## After Editing

Run `/mythicdrop reload` to apply changes.
