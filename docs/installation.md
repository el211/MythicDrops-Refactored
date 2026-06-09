# Installation Guide

This page walks you through installing MythicDrop step by step.

---

## Step 1 — Install Required Plugins

MythicDrop **requires** MythicMobs to be installed first.

1. Download **MythicMobs** from the official source and place it in your `plugins/` folder
2. (Optional) Download **LuckPerms** if you want different rewards per player rank
3. (Optional) Download **PlaceholderAPI** if you want scoreboards or GUIs showing quest/arena data
4. Start your server once to let these plugins generate their files, then stop it

---

## Step 2 — Install MythicDrop

1. Place the `MythicDropRefactored.jar` file into your `plugins/` folder
2. Start your server

On first launch, the plugin will create the following files automatically:

```
plugins/MythicDropRefactored/
  config.yml
  quests.yml
  top3damage.yml
  top5damage.yml
  announcement.yml
  effects.yml
  debug.yml
```

You will see a message in your server console confirming the plugin loaded.

---

## Step 3 — Verify Installation

In your server console or in-game, run:

```
/mythicdrop reload
```

If you see a success message, the plugin is working correctly.

---

## Step 4 — Configure Your First Drop

Open `plugins/MythicDropRefactored/config.yml` and add a reward for one of your MythicMobs.

See the [Drops Configuration Guide](configuration/drops.md) for full instructions.

---

## Updating the Plugin

1. Stop your server
2. Delete the old `.jar` from your `plugins/` folder
3. Place the new `.jar` in `plugins/`
4. Start your server

Your configuration files will **not** be deleted when updating.

---

## Uninstalling

1. Stop your server
2. Remove `MythicDropRefactored.jar` from `plugins/`
3. (Optional) Delete the `plugins/MythicDropRefactored/` folder to remove all data

---

## Troubleshooting Installation

**The plugin doesn't appear in `/plugins`:**
- Make sure you are running Spigot or Paper 1.21+
- Check that MythicMobs is installed and loaded first
- Look for errors in the console when the server starts

**Enable debug mode** to get more info in your console:
Open `plugins/MythicDropRefactored/debug.yml` and set:
```yaml
activate-debug: true
```
Then run `/mythicdrop reload`. Detailed logs will appear in your console.
