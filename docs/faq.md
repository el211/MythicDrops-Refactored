# FAQ & Troubleshooting

Answers to common questions and problems.

---

## General Questions

### Q: Do I need LuckPerms?

No. LuckPerms is **optional**. If you don't have it:
- All players are treated as the `default` group
- Just configure everything under `default` in your drop and quest configs

---

### Q: Do I need PlaceholderAPI?

No. PlaceholderAPI is **optional**. Without it:
- All plugin features work normally
- You just can't use `%mythicdrop_...%` placeholders in scoreboards/GUIs

---

### Q: What Minecraft versions are supported?

MythicDrop targets **Minecraft 1.21+** with the Spigot or Paper server software.

---

### Q: Do I need to restart the server to apply config changes?

No! Run `/mythicdrop reload` after editing any config file. No restart needed.

---

### Q: Where is player quest progress saved?

In `plugins/MythicDropRefactored/quest_data.yml`. This is created automatically and updated as players progress.

---

## Drop / Reward Problems

### Q: Players aren't getting any drops when they kill a mob

**Check these things in order:**

1. **Mob name:** Open `config.yml` and make sure the mob section name **exactly** matches the MythicMobs internal mob name (case-sensitive). Example: `SkeletonKing` ≠ `skeletonking`

2. **reward-processing:** Make sure at least one of these is `true`:
   ```yaml
   reward-processing:
     most-damage: false
     last-hit: true    # This should be true for most setups
   ```

3. **Top-damage system:** If the mob is listed in `top3damage.yml` or `top5damage.yml`, it won't use `config.yml` drops (unless `use-standard-rewards: true` is set).

4. **Enable debug mode:** Set `activate-debug: true` in `debug.yml` and reload. Check your console for detailed output about what's happening.

---

### Q: Players in the "vip" group are getting "default" rewards instead

**Check these things:**

1. LuckPerms is installed and working (`/lp info` to verify)
2. The player actually has the `vip` group in LuckPerms (`/lp user <name> info`)
3. The group name in your config exactly matches the LuckPerms group name (case-sensitive)
4. The player is online when the mob dies (offline group lookups return "default")

---

### Q: The `everyone-else-who-contributed` reward isn't working

Make sure:
1. The player dealt **at least** the `min-damage` amount specified
2. The player is **not** already in the top-N (those get rank rewards instead)
3. The mob is correctly listed in the top-damage config file

---

### Q: Commands in rewards aren't working

Test the command manually in your server console first:
```
give Steve diamond 1
```

If it works manually but not from the plugin, check:
- The command syntax in config (should not include the `/` at the start)
- `%player%` is typed exactly as shown (not `%Player%` or `{player}`)
- The player is online when the reward runs

---

## Quest Problems

### Q: Quest kills aren't being counted

**Check these things:**

1. **Mob name:** The `mob-name` in `quests.yml` must exactly match the MythicMobs mob name
2. **Damage-based credit:** If the player didn't deal the killing blow but dealt damage, they should still get credit (the plugin checks damage tracking as a fallback)
3. **Quest already completed:** Completed quests don't track further progress
4. **Debug mode:** Enable `activate-debug: true` in `debug.yml` to see detailed quest tracking logs

---

### Q: Quest progress was lost after a server restart

This shouldn't happen normally. Check:
1. The server shut down gracefully (not a crash — crashes can skip the save)
2. The `quest_data.yml` file exists in `plugins/MythicDropRefactored/`
3. The server has write permissions to the plugin folder

---

### Q: The quest GUI shows items in the wrong slots

The `slot` field in each quest's `conditions` section controls the position. Slots go from 0 (top-left) to 44 (bottom-right of row 5). Make sure:
- No two quests share the same slot number
- Slots are between 0 and 44

---

### Q: The quest GUI is empty / doesn't open

1. Make sure `quests.yml` has at least one quest configured
2. Run `/mythicdrop reload` after saving changes
3. Check the console for YAML syntax errors (indentation mistakes are common)

---

## Arena Problems

### Q: The boss isn't spawning when I create an arena

1. Make sure the mob name in `/marena setspawn` **exactly** matches the MythicMobs name
2. Make sure MythicMobs is installed and working (`/mm mobs spawn <mobname>` to test)
3. Check you're standing in a valid location (not inside a block)

---

### Q: The boss isn't respawning after it dies

1. Check the `respawn` value — it's in **seconds** (`60` = 1 minute)
2. Make sure `respawn` is `> 0` (use at least `1`)
3. Check that ArenaManager isn't throwing errors in the console

---

### Q: The arena is listed but `/marena list` shows no mobs alive

The boss may have been despawned. It will respawn according to the respawn timer. If it never comes back, check console for errors related to `ArenaManager`.

---

## Announcements

### Q: The death announcement isn't appearing in chat

1. Make sure `announce-on-death: true` in `announcement.yml`
2. Check `announce-specific-mob` — if the mob is listed as `false`, it won't announce
3. Run `/mythicdrop reload` after changes

---

### Q: The announcement appears for mobs I don't want

Add the mob to `announce-specific-mob` with value `false`:

```yaml
announce-specific-mob:
  TrashMob: false
  CommonZombie: false
```

---

## YAML Syntax Help

Most config problems are caused by **incorrect YAML formatting**. Here are the most common mistakes:

### Wrong indentation (spaces, not tabs!)
```yaml
# WRONG — mixed tabs and spaces
SkeletonKing:
	drops:         # TAB character — will break YAML!
    default:

# CORRECT — 2 spaces per indent level
SkeletonKing:
  drops:
    default:
```

### Missing colon
```yaml
# WRONG
mob-name "SkeletonKing"

# CORRECT
mob-name: "SkeletonKing"
```

### Wrong list format
```yaml
# WRONG
rewardtop3:
  SkeletonKing

# CORRECT
rewardtop3:
  - SkeletonKing
```

### Special characters in values
If a value contains `:`, `#`, `[`, `]`, `{`, `}`, wrap it in quotes:
```yaml
message: "&a[VIP] You got a reward!"   # Square brackets need quotes
```

---

## Getting Help

1. Enable debug mode (`activate-debug: true` in `debug.yml`) and check your server console
2. Use a YAML validator (search "YAML validator online") to check your config files for syntax errors
3. Double-check mob names by opening your MythicMobs config and copying the exact name
