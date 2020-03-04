
# Mobzy

![Release](https://jitpack.io/v/MineInAbyss/Mobzy.svg)](https://jitpack.io/#MineInAbyss/Mobzy)
![CI](https://github.com/MineInAbyss/Mobzy/workflows/Java%20CI/badge.svg)


### Intro

This is a plugin for Spigot/Paper 1.13.2, which allows custom entities to be spawned. It works specifically for the purpose of loading  entities with custom models.

The way entities are registered with NMS means the server sees them as completely new entity types, but because the client does not know them, the plugin relies on LibsDisguises to trick the client.

It even goes a step further to implement custom hitboxes for larger entities, since normally these are also handled by the client.

### Plans

An API is being worked on, and while available on JitPack, it is not intended for use in other plugins yet.