# Custom Mobs

### Intro

This is a plugin for Spigot/Paper 1.13.2, which allows custom entities to be spawned. It works specifically for the purpose of loading  entities with custom models.

The way entities are registered with NMS means the server sees them as completely new entity types, but because the client does not know them, the plugin relies on LibsDisguises to trick the client.

It even goes a step further to implement custom hitboxes for larger entities, since normally these are also handled by the client.

### Plans

Its use case is currently very specific to the Mine In Abyss server, but these mobs will eventually be separated into their own plugin, and this one kept as an API of sorts.