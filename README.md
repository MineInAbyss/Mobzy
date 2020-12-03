# Mobzy

![CI](https://github.com/MineInAbyss/Mobzy/workflows/Java%20CI/badge.svg)

### Overview

Mobzy is a WIP Spigot/Paper plugin for creating custom NMS entities built on top of our own [Geary](https://github.com/MineInAbyss/Geary) Entity Component System (ECS). It makes coding complex entities simpler by breaking them down into many modular components. Mobzy then provides a configuration system for making custom entity types out of these components.

Mobzy also provides an API for injecting custom NMS entity types into the server, and packet interception to allow clients to see them. Some of these more general-use features may be moved into their own library eventually.

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
- Annotation processor that makes extending any NMS entity as a custom mob simpler (will likely be rewritten as a compiler plugin once [ksp](https://github.com/google/ksp) is stable).
- Many premade components, with more to come in the future.

## Future plans

### Goal Oriented Action Planners

We would like to write our own AI system that uses GOAPs to create configurable emergent behaviour that fits nicely with Minecraft's existing pathfinder goal system.

Essentially, you would be able to code actions with conditions and outcomes, then given a list of possible actions, the system will pathfind its way from a goal to some chain of actions whose conditions are met. These goals can then directly be added as pathfinder goals, ordered by priority.

## Usage

We have [Github packages](https://github.com/MineInAbyss/Mobzy/packages) set up for use with gradle/maven, however the API isn't properly maintained yet. Many things will change as the ECS is being built.

We have started working on a [wiki](https://github.com/MineInAbyss/Mobzy/wiki) but it won't be complete for a while. You can ask us questions in `#plugin-dev` on our [Discord](https://discord.gg/QXPCk2y) server, or come help with development there!
