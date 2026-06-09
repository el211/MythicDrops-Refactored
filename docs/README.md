# MythicDrop - Complete Documentation

Welcome to the **MythicDrop** plugin documentation! This guide is written for complete beginners, so no programming knowledge is needed.

---

## What is MythicDrop?

MythicDrop is a **Minecraft server plugin** that works alongside **MythicMobs** to give you powerful control over:

- **Custom item/command drops** when players kill MythicMobs
- **Ranked rewards** based on who dealt the most damage to a boss
- **Quests** — players must kill a certain number of mobs to earn rewards
- **Arenas** — automatically respawning boss arenas
- **Visual effects** (fireworks, sounds) when a boss dies
- **Damage leaderboard announcements** after a boss dies
- **PlaceholderAPI** support for scoreboards and GUIs

---

## Requirements

Before installing MythicDrop, make sure you have:

| Software | Required? | Notes |
|---|---|---|
| Spigot / Paper 1.21+ | **Yes** | Your server software |
| MythicMobs | **Yes** | The mob plugin MythicDrop hooks into |
| LuckPerms | Optional | Needed for group-based reward tiers |
| PlaceholderAPI | Optional | Needed for scoreboard/GUI placeholders |

---

## Documentation Index

| File | What it covers |
|---|---|
| [Installation](installation.md) | How to install the plugin step by step |
| [Commands](commands.md) | Every command and permission |
| [Configuration: Drops](configuration/drops.md) | How to give rewards when a mob dies |
| [Configuration: Quests](configuration/quests.md) | How to set up the quest system |
| [Configuration: Top Damage Rewards](configuration/top-rewards.md) | Rank-based rewards (Top 3, Top 5, Top N) |
| [Configuration: Arenas](configuration/arenas.md) | Auto-respawning boss arenas |
| [Configuration: Effects](configuration/effects.md) | Fireworks and sounds on boss death |
| [Configuration: Announcements](configuration/announcements.md) | Damage leaderboard chat messages |
| [Placeholders](placeholders.md) | All PlaceholderAPI placeholders |
| [FAQ](faq.md) | Common questions and troubleshooting |

---

## Quick Start (5 minutes)

1. Drop the plugin `.jar` into your `plugins/` folder
2. Restart your server — config files will be created automatically
3. Open `plugins/MythicDropRefactored/config.yml`
4. Add a reward for one of your MythicMobs (see [Drops guide](configuration/drops.md))
5. Reload with `/mythicdrop reload`

That's it! See the individual pages for detailed configuration.

---

## Folder Structure

After the plugin runs for the first time, you will see this in your `plugins/` folder:

```
plugins/
  MythicDropRefactored/
    config.yml          <-- Main drops configuration
    quests.yml          <-- Quest definitions
    top3damage.yml      <-- Top-3 boss reward system
    top5damage.yml      <-- Top-5 boss reward system
    announcement.yml    <-- Chat announcements after boss death
    effects.yml         <-- Fireworks & sounds on boss death
    debug.yml           <-- Turn on debug logging
    arenas.yml          <-- Created automatically when you make arenas
    quest_data.yml      <-- Player quest progress (auto-created)
```

You can create additional files like `top7damage.yml`, `top10damage.yml`, etc. for custom rankings.
