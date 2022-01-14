<div align="center">

# Mobzy    
[![Java CI with Gradle](https://github.com/MineInAbyss/Mobzy/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/MineInAbyss/Mobzy/actions/workflows/gradle-ci.yml)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/mobzy/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/mobzy)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://github.com/MineInAbyss/Mobzy/wiki)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://github.com/MineInAbyss/MineInAbyss/wiki/Setup-and-Contribution-Guide)
</div>

### Overview

Mobzy is a PaperMC plugin for injecting custom NMS entities into the server, built on top of our own [Geary](https://github.com/MineInAbyss/Geary) Entity Component System (ECS). We use it to break down complex entities into many modular components in config files.

Our plan is to eventually phase out use of vanilla entities in favor of fully custom, platform-agnostic ones that get sent through packets.

![Custom Mobs](https://media.discordapp.net/attachments/464678554681081856/625036159772524582/2019-09-21_19.39.27.png?width=1210&height=681)

## Features

### Modular behaviours

ECS lets us split up many mob behaviours into individual components, making code easier to maintain and more reusable. More info can be found in [Geary's readme](https://github.com/MineInAbyss/Geary).

### Config based

Thanks to kotlinx.serialization our component are automatically serializable without reflection. This means all components can be read from a config file or stored in a mob's persistent data container. We then provide some extra options for adding pathfinder goals, or inheriting from different entity types. You can read more on the [Configuring Custom Entities](https://github.com/MineInAbyss/Mobzy/wiki/Configuring-Custom-Entities) wiki page.

### NMS Wrappers

Mobzy provides many serializable wrappers for pathfinders which can be used by other plugins to avoid going through NMS. We also provide some extension functions and typealiases to make dealing with NMS easier. We will likely put these into their own API later, with proper documentation. 

### Other

- Custom hitboxes (Minecraft normally lets the client handle that, so it wouldn't work on entity types it doesn't know).
- Custom spawning system.
- Many premade components, with more to come in the future.

## Future plans

### Goal Oriented Action Planners

We would like to write our own AI system that uses GOAPs to create configurable emergent behaviour that fits nicely with Minecraft's existing pathfinder goal system.

Essentially, you would be able to code actions with conditions and outcomes, then given a list of possible actions, the system will pathfind its way from a goal to some chain of actions whose conditions are met. These goals can then directly be added as pathfinder goals, ordered by priority.

## Usage

We have a maven repo set up, however the API isn't properly maintained yet. Many things will change as the ECS is being built.

### Gradle

```groovy
repositories {
    maven { url 'https://repo.mineinabyss.com/releases' }
}

dependencies {
    implementation 'com.mineinabyss:mobzy:<version>'
}
```

## Project Wiki

We have started working on a [wiki](https://github.com/MineInAbyss/Mobzy/wiki) but it won't be complete for a while. You can ask us questions in `#plugin-dev` on our [Discord](https://discord.gg/QXPCk2y) server, or come help with development there!
