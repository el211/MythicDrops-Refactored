# Drops Configuration (`config.yml`)

This is the main configuration file. It controls what happens when a player kills a MythicMob — what commands run, what items they receive, and which players are eligible.

---

## File Location

```
plugins/MythicDropRefactored/config.yml
```

---

## Basic Structure

```yaml
reward-processing:
  most-damage: false
  last-hit: true

SkeletonKing:
  flexible-reward-mode: false
  drops:
    default:
      drop1:
        command: "give %player% diamond 1"
        chance: 1.0
        message: "&b&lYou got a diamond!"
```

Let's break down each section.

---

## `reward-processing` — Who Gets the Reward?

This section controls **which player** receives the drop when the mob dies.

```yaml
reward-processing:
  most-damage: false   # true = the player who dealt the most total damage
  last-hit: true       # true = the player who dealt the killing blow
```

| Setting | Value | Effect |
|---|---|---|
| `most-damage` | `true` | The player who dealt the most damage gets the reward |
| `most-damage` | `false` | Ignore total damage |
| `last-hit` | `true` | The player who killed the mob gets the reward |
| `last-hit` | `false` | Ignore the killing blow |

**Rules (applied in order):**
1. If `most-damage: true` → most damage dealer gets rewards
2. Else if `last-hit: true` → last hitter gets rewards
3. Otherwise → no rewards given

> **Tip:** For solo-kill mobs, use `last-hit: true`. For boss fights where many players attack, use `most-damage: true` or the [Top Damage system](top-rewards.md) instead.

---

## Mob Drop Sections

Each MythicMob gets its own section, using its **internal MythicMobs name** as the key.

```yaml
SkeletonKing:         # <-- This must match the mob's name in MythicMobs exactly
  flexible-reward-mode: false
  drops:
    default:          # <-- Player group (from LuckPerms)
      drop1:          # <-- Any unique name for this reward
        command: "give %player% diamond 1"
        chance: 1.0
        message: "&b&lYou got a diamond!"
```

---

## `flexible-reward-mode`

Controls how multiple drops in the same group are handled.

```yaml
flexible-reward-mode: false
```

| Value | Behavior |
|---|---|
| `false` | Only the first drop that passes its chance check is given. Stops after that. |
| `true` | Every drop rolls its own chance independently. Players can get multiple drops. |

**Example with `flexible-reward-mode: false`:**
- `drop1` has a 100% chance → always given → stops here, `drop2` never runs
- If `drop1` had 50% chance and failed → tries `drop2`

**Example with `flexible-reward-mode: true`:**
- `drop1` (50% chance) and `drop2` (30% chance) both roll independently
- A player could get both, one, or neither

---

## The `drops` Section

Inside `drops`, you define **player groups**. If you use LuckPerms, each group can get different rewards.

```yaml
drops:
  default:     # Players in the "default" LuckPerms group
    drop1:
      command: "give %player% diamond 1"
      chance: 1.0
      message: "&aYou got a diamond!"

  vip:         # Players in the "vip" LuckPerms group
    drop1:
      command: "give %player% diamond 3"
      chance: 1.0
      message: "&b[VIP] You got 3 diamonds!"
```

> **No LuckPerms?** Always use `default` as your only group. All players will use it.

---

## Drop Entry Options

Each drop entry has three fields:

```yaml
drop1:                              # Unique name (can be anything)
  command: "give %player% stone 1"  # Console command to run
  chance: 0.5                       # Probability: 0.0 = never, 1.0 = always
  message: "&aYou got stone!"       # (Optional) Message sent to the player
```

### `command`

Any Minecraft console command. Use `%player%` where the player's name should go.

```yaml
command: "give %player% diamond 5"
command: "eco give %player% 500"
command: "crate key give %player% legendary 1"
```

### `chance`

A number between `0.0` and `1.0`:

| Value | Meaning |
|---|---|
| `1.0` | 100% — always happens |
| `0.5` | 50% chance |
| `0.25` | 25% chance |
| `0.0` | 0% — never happens |

### `message`

A chat message sent only to the player who gets the reward. Supports color codes with `&`.

```yaml
message: "&b&lYou received a legendary item!"
```

This field is **optional** — remove the line if you don't want a message.

---

## Full Example

```yaml
reward-processing:
  most-damage: false
  last-hit: true

# --- Skeleton King Boss ---
SkeletonKing:
  flexible-reward-mode: true    # All drops roll independently
  drops:
    default:                    # Non-VIP players
      gold_reward:
        command: "give %player% gold_ingot 5"
        chance: 1.0
        message: "&eYou got 5 gold ingots!"
      diamond_bonus:
        command: "give %player% diamond 1"
        chance: 0.25            # 25% chance to also get a diamond
        message: "&bBonus! You got a diamond!"
      money_reward:
        command: "eco give %player% 200"
        chance: 1.0
        message: "&a+$200"

    vip:                        # VIP players get better rewards
      gold_reward:
        command: "give %player% gold_ingot 10"
        chance: 1.0
        message: "&eVIP: You got 10 gold ingots!"
      diamond_bonus:
        command: "give %player% diamond 3"
        chance: 0.5             # 50% chance for VIPs
        message: "&bVIP Bonus! You got 3 diamonds!"
      money_reward:
        command: "eco give %player% 500"
        chance: 1.0
        message: "&a+$500"

# --- Cave Spider ---
CaveSpider:
  flexible-reward-mode: false   # Stop after first successful drop
  drops:
    default:
      common:
        command: "give %player% string 4"
        chance: 0.8
        message: "&7You got some string."
      rare:
        command: "give %player% spider_eye 1"
        chance: 0.2
        message: "&cRare drop! Spider Eye!"
```

---

## How to Apply Changes

After editing `config.yml`, reload the plugin:
```
/mythicdrop reload
```

---

## Common Mistakes

| Problem | Solution |
|---|---|
| Mob name doesn't match | Open MythicMobs config, copy the **exact** mob name (case-sensitive) |
| Players not getting drops | Check `reward-processing` settings; make sure the mob name matches |
| All players getting the same (wrong) rewards | Check LuckPerms groups — if a group isn't in config, it uses `default` |
| Command not working | Test the command manually in console first (replace `%player%` with a real name) |
