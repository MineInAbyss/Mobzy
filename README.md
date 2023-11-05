<div align="center">

# Mobzy
[![Java CI with Gradle](https://github.com/MineInAbyss/Mobzy/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/MineInAbyss/Mobzy/actions/workflows/gradle-ci.yml)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/mobzy/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/mobzy)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://github.com/MineInAbyss/Mobzy/wiki)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://github.com/MineInAbyss/MineInAbyss/wiki/Setup-and-Contribution-Guide)
</div>

Mobzy is a [Paper](https://papermc.io/) plugin for creating custom mobs with config files. We use [Geary](https://github.com/MineInAbyss/geary-papermc) to break down complex entities into small components. We provide many components to modify vanilla behaviour, for new game features check out [Geary-addons](https://github.com/MineInAbyss/Geary-addons).

## Features

- Extend any existing entity (including NMS entities if registered by another plugin)
- Simple ModelEngine support
- Customize pathfinder goals
- Our own custom spawning system

## Example

[**`plugins/Geary/mineinabyss/mobs/hostile/kuongatari.yml`**](https://github.com/MineInAbyss/server-config/blob/master/servers/minecraft/plugins/Geary/mineinabyss/mobs/hostile/kuongatari.yml)
```yaml
- !<geary:inherit> # Inherits components from another prefab
  from: [mineinabyss:hostile]
- !<mobzy:type> # Tells Minecraft which entity to use under the hood. You may register a custom type with NMS.
  baseClass: minecraft:zombie
  creatureType: MONSTER
- !<geary:display_name> "<#1FB53D>Kuongatari" # Sets a colored display name
- !<mobzy:modelengine> # Uses a ModelEngine model
  modelId: kuongatari
- !<mobzy:pathfinders> # Sets some pathfinder goals
  targets:
    1: !<mobzy:target.attacker>
      range: 200
    2: !<mobzy:target.nearby_player>
      range: 10
  goals:
    1: !<minecraft:behavior.melee_attack>
      seeThroughWalls: true
    2: !<minecraft:behavior.leap_at_target>
      jumpHeight: 0.6
    4: !<minecraft:behavior.random_stroll_land>
```

## Project Wiki

We have an old [wiki](https://github.com/MineInAbyss/Mobzy/wiki) that we want to update soon. For now, you can ask us questions in `#plugin-dev` on our [Discord](https://discord.gg/QXPCk2y) server, or come help with development there!

You may also find our [generated docs](https://mineinabyss.com/Mobzy/) useful
