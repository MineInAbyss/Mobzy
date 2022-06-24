<div align="center">

# Mobzy
[![Java CI with Gradle](https://github.com/MineInAbyss/Mobzy/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/MineInAbyss/Mobzy/actions/workflows/gradle-ci.yml)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/mobzy/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/mobzy)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://github.com/MineInAbyss/Mobzy/wiki)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://github.com/MineInAbyss/MineInAbyss/wiki/Setup-and-Contribution-Guide)
</div>

## Overview

Mobzy is a PaperMC plugin for creating custom Minecraft entities. It is built on top of [Geary](https://github.com/MineInAbyss/Geary), our own Entity Component System (ECS). We use it to break down complex entities into many modular components in config files.

## Features

### Modular behaviours

ECS lets us split up many mob behaviours into individual components, making code easier to maintain and more reusable. More info can be found in [Geary's readme](https://github.com/MineInAbyss/Geary).

### Config based

We can easily create serializable components, which lets us read from a config file or store data on a mob's persistent data container.

Here's an example config from our own server:
```yaml
- !<geary:inherit> # Inherits components from another prefab
  from: [mineinabyss:hostile]
- !<mobzy:type> # Tells Minecraft which entity to use under the hood. You may register a custom type with NMS.
  baseClass: minecraft:zombie
  creatureType: MONSTER
- !<geary:display_name> "<#1FB53D>Kuongatari" # Sets a colored display name
- !<mobzy:modelengine> # Uses a ModelEngine model for this mob
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

### Other

- Support for single models or ModelEngine (all the packet manipulation is handled for you!)
- Custom spawning system
- Many premade components (see [Geary-addons](https://github.com/MineInAbyss/Geary-addons/))

## Usage

You may use Mobzy along with Geary through our maven repo, however the API isn't stable yet.

### Gradle

```kotlin
repositories {
    maven("https://repo.mineinabyss.com/releases")
}

dependencies {
    compileOnly("com.mineinabyss:geary:<version>")
    compileOnly("com.mineinabyss:mobzy:<version>")
}
```

## Project Wiki

We have started working on a [wiki](https://github.com/MineInAbyss/Mobzy/wiki) but it won't be complete for a while. You can ask us questions in `#plugin-dev` on our [Discord](https://discord.gg/QXPCk2y) server, or come help with development there!

## Future plans

### Goal Oriented Action Planners

We would like to write our own AI system that uses GOAPs to create configurable emergent behaviour that fits nicely with Minecraft's existing pathfinder goal system.

Essentially, you would be able to code actions with conditions and outcomes, then given a list of possible actions, the system will pathfind its way from a goal to some chain of actions whose conditions are met. These goals can then directly be added as pathfinder goals, ordered by priority.

### Fully custom entities

Our plan is to eventually phase out use of vanilla entities in favor of fully custom, platform-agnostic ones that get sent through packets.
