# Death Effects (`effects.yml`)

The effects system lets you play **fireworks** and **sounds** at the location where a MythicMob dies. This makes boss deaths feel more dramatic and rewarding.

---

## File Location

```
plugins/MythicDropRefactored/effects.yml
```

---

## Basic Structure

```yaml
SkeletonKing:
  effects:
    firework_display:
      type: FIREWORK
      power: 2
      color: BLUE
      flicker: true
      trail: true

    death_roar:
      type: SOUND
      sound: ENTITY_ENDER_DRAGON_DEATH
      volume: 1.0
      pitch: 0.7
```

Each mob can have **multiple effects** — just add them as separate entries under `effects:`.

---

## Effect Types

### `FIREWORK`

Launches a firework at the mob's death location.

```yaml
firework_display:
  type: FIREWORK
  power: 2        # Flight power (1 = low, 2 = medium, 3 = high)
  color: BLUE     # Firework color (see color list below)
  flicker: true   # true = sparkle effect
  trail: true     # true = trail behind firework
```

| Field | Description | Options |
|---|---|---|
| `power` | How high the firework goes before exploding | `1`, `2`, `3` |
| `color` | The color of the firework burst | See color list below |
| `flicker` | Whether the firework sparkles | `true` / `false` |
| `trail` | Whether the firework leaves a trail | `true` / `false` |

#### Available Colors

```
WHITE, SILVER, GRAY, BLACK,
RED, MAROON, YELLOW, OLIVE,
LIME, GREEN, AQUA, TEAL,
BLUE, NAVY, FUCHSIA, PURPLE,
ORANGE
```

---

### `SOUND`

Plays a sound at the mob's death location. All nearby players hear it.

```yaml
death_sound:
  type: SOUND
  sound: ENTITY_ENDER_DRAGON_DEATH   # Minecraft sound name
  volume: 1.0                        # How loud (0.0 to 2.0+)
  pitch: 1.0                         # Tone (0.5 = lower, 2.0 = higher)
```

| Field | Description |
|---|---|
| `sound` | The Minecraft sound event name (uppercase, underscores) |
| `volume` | Volume level. `1.0` is normal. Higher values increase range. |
| `pitch` | Pitch/tone. `1.0` is normal. `0.5` is low, `2.0` is high. |

#### Common Sound Names

| Sound | Description |
|---|---|
| `ENTITY_ENDER_DRAGON_DEATH` | Dragon death roar |
| `ENTITY_WITHER_DEATH` | Wither explosion death |
| `ENTITY_LIGHTNING_BOLT_THUNDER` | Thunder crack |
| `ENTITY_GENERIC_EXPLODE` | Explosion sound |
| `UI_TOAST_CHALLENGE_COMPLETE` | Achievement fanfare |
| `ENTITY_PLAYER_LEVELUP` | Level-up chime |
| `BLOCK_BEACON_POWER_SELECT` | Beacon activation |
| `ENTITY_ENDER_DRAGON_GROWL` | Dragon growl |

You can find the full list of Minecraft sound names at the [Minecraft Wiki](https://minecraft.wiki/w/Sounds.json).

---

## Multiple Effects

You can add as many effects as you want. Give each one a unique name:

```yaml
SkeletonKing:
  effects:
    firework_1:
      type: FIREWORK
      power: 2
      color: BLUE
      flicker: true
      trail: false

    firework_2:
      type: FIREWORK
      power: 3
      color: WHITE
      flicker: false
      trail: true

    epic_sound:
      type: SOUND
      sound: ENTITY_ENDER_DRAGON_DEATH
      volume: 1.5
      pitch: 0.8

    victory_chime:
      type: SOUND
      sound: UI_TOAST_CHALLENGE_COMPLETE
      volume: 1.0
      pitch: 1.0
```

---

## Full Example

```yaml
# Skeleton King — blue/white fireworks + dragon roar
SkeletonKing:
  effects:
    blue_firework:
      type: FIREWORK
      power: 2
      color: BLUE
      flicker: true
      trail: true
    white_firework:
      type: FIREWORK
      power: 3
      color: WHITE
      flicker: false
      trail: true
    death_sound:
      type: SOUND
      sound: ENTITY_WITHER_DEATH
      volume: 1.0
      pitch: 1.0

# Ancient Dragon — red/purple fireworks + dragon death sound
AncientDragon:
  effects:
    red_firework:
      type: FIREWORK
      power: 3
      color: RED
      flicker: true
      trail: true
    purple_firework:
      type: FIREWORK
      power: 2
      color: PURPLE
      flicker: true
      trail: false
    dragon_death:
      type: SOUND
      sound: ENTITY_ENDER_DRAGON_DEATH
      volume: 2.0
      pitch: 0.7
    victory:
      type: SOUND
      sound: UI_TOAST_CHALLENGE_COMPLETE
      volume: 1.0
      pitch: 1.0

# Cave Spider — simple sound only
CaveSpider:
  effects:
    pop_sound:
      type: SOUND
      sound: ENTITY_GENERIC_EXPLODE
      volume: 0.5
      pitch: 1.5
```

---

## After Editing

Run `/mythicdrop reload` to apply your changes.

---

## Tips

- For major bosses, use multiple fireworks with different colors and heights for an impressive display
- Set `volume` higher than `1.0` to broadcast the sound over a larger distance
- A `pitch` below `1.0` makes sounds deeper/more dramatic; above `1.0` makes them higher/brighter
- Combine a low-pitched dragon death sound with colorful fireworks for epic boss kill moments
