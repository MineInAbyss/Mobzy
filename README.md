# Mobzy

![CI](https://github.com/MineInAbyss/Mobzy/workflows/Java%20CI/badge.svg)

### Overview

This is a plugin for Spigot/Paper 1.15.2, which allows custom entities to registered with the Minecraft server. It works more specifically for the purpose of loading  entities with custom models, but provides many classes for avoiding NMS classes when making pathfinders, registering entities, and creating them.

You still need NMS to use this project, but it aims to makes updating between Minecraft versions nicer. You can see an implementation of this project [here](https://github.com/MineInAbyss/AbyssalCreatures).

The way entities are registered with NMS means the server sees them as completely new entity types. However, because the client does not know these types, the plugin intercepts the packet sent by the server to show a mob the client already knows, like an invisible zombie with a custom modelled item which creates truly unique creatures:

![Custom Mobs](https://media.discordapp.net/attachments/464678554681081856/625036159772524582/2019-09-21_19.39.27.png?width=1210&height=681)

### Use

- We have [Github packages](https://github.com/MineInAbyss/Mobzy/packages) set up for use with gradle/maven, however the API isn't properly maintained, many things may change, but things are mostly calming down. (no promises though!)
- There currently isn't a wiki explaining how to use things yet. We'll get one done once the plugin is properly released. You can ask about things in #plugin-dev on our [Discord](https://discord.gg/QXPCk2y).

### Additional features

- Custom hitboxes (Minecraft normally lets the client handle that, so it wouldn't work on entity types it doesn't know).
- A small pathfinder goal API, with some premade pathfinders for our own mobs.
- Custom mob spawning system
- Configuration for mob drops and spawn locations