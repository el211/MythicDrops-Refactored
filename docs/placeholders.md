# PlaceholderAPI Placeholders

If you have **PlaceholderAPI** installed, MythicDrop provides placeholders you can use in scoreboards, GUIs, holograms, chat formats, and more.

---

## Requirements

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) must be installed on your server
- After installing PlaceholderAPI, run `/mythicdrop reload` once to register MythicDrop's placeholders

---

## Quest Placeholders (Per-Player)

These return information about a **specific player's progress** on a specific quest.

Replace `[id]` with the quest ID from your `quests.yml` (the key at the top of each quest, e.g., `cave_spider_slayer`).

| Placeholder | Description | Example Output |
|---|---|---|
| `%mythicdrop_quest_[id]_progress%` | Current kill count | `7` |
| `%mythicdrop_quest_[id]_required%` | Total kills needed | `10` |
| `%mythicdrop_quest_[id]_remaining%` | Kills still needed (`0` when done) | `3` |
| `%mythicdrop_quest_[id]_percent%` | Completion percentage (0-100) | `70` |
| `%mythicdrop_quest_[id]_completed%` | Whether quest is done | `true` or `false` |
| `%mythicdrop_quest_[id]_name%` | Quest display name (colors stripped) | `Cave Spider Slayer` |
| `%mythicdrop_quest_[id]_mob%` | Target mob name | `CaveSpider` |

**Example:** For a quest with ID `cave_spider_slayer`:
```
%mythicdrop_quest_cave_spider_slayer_progress%   → 7
%mythicdrop_quest_cave_spider_slayer_required%   → 10
%mythicdrop_quest_cave_spider_slayer_percent%    → 70
%mythicdrop_quest_cave_spider_slayer_completed%  → false
```

---

## Quest Summary Placeholders (Per-Player)

These return **overall quest stats** for a player across all quests.

| Placeholder | Description | Example Output |
|---|---|---|
| `%mythicdrop_quests_total%` | Total number of quests defined in config | `5` |
| `%mythicdrop_quests_completed%` | How many quests this player has completed | `2` |
| `%mythicdrop_quests_remaining%` | Quests the player hasn't completed yet | `3` |
| `%mythicdrop_quests_percent%` | Overall completion percentage (0-100) | `40` |

---

## Arena Placeholders

These return information about arenas. Replace `[name]` with your arena name.

| Placeholder | Description | Example Output |
|---|---|---|
| `%mythicdrop_arena_count%` | Total number of configured arenas | `3` |
| `%mythicdrop_arena_live_count%` | How many arenas have a live boss right now | `2` |
| `%mythicdrop_arena_[name]_exists%` | Does this arena exist? | `true` or `false` |
| `%mythicdrop_arena_[name]_mob%` | Mob type configured for this arena | `SkeletonKing` |
| `%mythicdrop_arena_[name]_world%` | World where this arena is located | `world` |
| `%mythicdrop_arena_[name]_alive%` | Is the boss currently alive? | `true` or `false` |

**Example:** For an arena named `skeleton_boss_room`:
```
%mythicdrop_arena_skeleton_boss_room_exists%  → true
%mythicdrop_arena_skeleton_boss_room_mob%     → SkeletonKing
%mythicdrop_arena_skeleton_boss_room_alive%   → true
```

---

## Live Damage Placeholders (Per-Player)

These update in real-time as players fight. Useful for boss fight scoreboards.

| Placeholder | Description | Example Output |
|---|---|---|
| `%mythicdrop_live_damage%` | This player's total damage dealt to all active mobs | `1250` |
| `%mythicdrop_live_rank%` | This player's current rank among all damage dealers | `1` |
| `%mythicdrop_live_top1_name%` | Name of the #1 damage dealer | `Steve` |
| `%mythicdrop_live_top1_damage%` | Damage dealt by #1 | `1250` |
| `%mythicdrop_live_top2_name%` | Name of the #2 damage dealer | `Alex` |
| `%mythicdrop_live_top2_damage%` | Damage dealt by #2 | `980` |
| `%mythicdrop_live_top3_name%` | Name of the #3 damage dealer | `Notch` |
| `%mythicdrop_live_top3_damage%` | Damage dealt by #3 | `520` |

You can use `top1` through any number (e.g., `top5`, `top10`).

---

## LuckPerms Group Placeholder

| Placeholder | Description | Example Output |
|---|---|---|
| `%mythicdrop_group%` | The player's primary LuckPerms group | `vip` |

Returns `offline` if the player is not online.

---

## Example Scoreboard Setup

If you use a scoreboard plugin like **FastBoard** or **ScoreboardReborn**, you can show live boss fight data:

```yaml
# Example scoreboard lines during a boss fight
lines:
  - "&6&l--- BOSS FIGHT ---"
  - "&fYour damage: &c%mythicdrop_live_damage%"
  - "&fYour rank: &e#%mythicdrop_live_rank%"
  - ""
  - "&e#1 &f%mythicdrop_live_top1_name%: &c%mythicdrop_live_top1_damage%"
  - "&e#2 &f%mythicdrop_live_top2_name%: &c%mythicdrop_live_top2_damage%"
  - "&e#3 &f%mythicdrop_live_top3_name%: &c%mythicdrop_live_top3_damage%"
  - ""
  - "&fQuests done: &a%mythicdrop_quests_completed%&f/&a%mythicdrop_quests_total%"
```

---

## Example Quest Progress HUD

Show a player's quest progress on a scoreboard:

```yaml
lines:
  - "&6Quest Progress"
  - ""
  - "&fSpider Slayer: &a%mythicdrop_quest_cave_spider_slayer_progress%&7/&f%mythicdrop_quest_cave_spider_slayer_required%"
  - "&fSkeleton King: &a%mythicdrop_quest_skeleton_king_slayer_progress%&7/&f%mythicdrop_quest_skeleton_king_slayer_required%"
  - ""
  - "&fTotal: &a%mythicdrop_quests_completed%&f/&a%mythicdrop_quests_total% &7complete"
```
