# Outline (for the old system)
#TODO: Rewrite this outline as a readme!

## Mob Type

The basis for a mob is a MobType.

A `MobType` must be registered, which is done through enums. The type stores other info, such as the mob name, a model id (used to figure out the damage value of the model which corresponds with this mob).

> Right now there's an enum for Ground mobs, NPCs, and Flying mobs. It's kind of annoying to have to split them up like this, but some things like NPCs are definitely better off separate from other stuff, just because of how many of them there are. I just want to know if there's a cleaner way to use them.

## Mob Behaviours

The type also stores one more valuable item: a `MobBehaviour`. Each custom mob has a behaviour attached to it, which essentially attaches either a listener or task to that specific mob type. When an event occurs, we can check whether the entity affected contains a behaviour, and do something for it. For instance, `DeathBehaviour` allows us to do an action when a mob dies. By default, it spawns a few cloud particles, and then gets a mob's registered drops, stored inside the behaviour. We can also perform any additional actions by Overriding the `onDeath` method inside a behaviour class
which implements `DeathBehaviour`.

## Registering Mobs with Minecraft

Minecraft requires you to register mobs by creating `EntityTypes` objects for each new mob, so they can later be accessed as parameters. The current system is more autonomous, designed to automatically register these types into a HashMap, so we can acess them with strings (or by passing an enum's item, since we can get the mob's name from there!).

`SpawnListener` contains a method which will search through this map, and spawn an entity given a string for its name, and a location. For example:

```Java
SpawnListener.spawnEntity(GroundMobType.FUWAGI, l);
```

> I probably should move the spawnEntity method out of the spawn listener.

## Associating entities with MobTypes

When spawning a mob, we are directly able to set the entity's name, but a name is not enough to identify mobs since they can be renamed by players ingame. Instead we use scoreboard tags, which can only be assigned through code/command blocks.

Internally, we actually convert the name into a mob "ID", by capitalizing all letters and converting spaces to underscores, to follow Minecraft's `EntityTypes` naming scheme, and avoid problems with multi word names. This is useful when spawning new entities, where we need the ID to spawn the entity in, but also want to set the entity's name to its actual name.

Inside each entity's constructor we have to set add this ID tag. Unfortunately, this has to be done manually, because we can't identify what the current mob's`MobType` is, since we haven't set the tag yet. You might think to use the entity's name as an identifier, which has already been set when creating the entity, but this doesn't work because the name is only set after the entity is created. Instead, to do this, I can only think of Overriding the `setCustomName` method, listening to the name set, and figuring out the type from there. We also must make sure this Override only runs once, since otherwise a player could still change a mob's type by renaming it. We'll get to why it's important to be able to do this automatically in a bit.

> Is there some better automated way of setting the mob's ID tag?

## Mob Template classes

##### Why are they useful?

Since some mobs are really similar, we can create template classes, which extend a specific entity, say `EntityPig` for `PassiveMob`. We can then extend these templates, e.x. like `Neritantan` does, and add our own code on top of them, such as pathfinders or initAttributes (mob health, speed, etc...).

##### We don't only extend templates

Another great feature of template classes is that we can define multiple `MobType`s in an enum, which uses the same class, but can tweak small parts of the mob, such as its name, so long as that `MobType` is able to store those tweaks. A great example of why this is useful can be seen in the `NPC` mob. Each NPC has essentially TWO differences, its name and the model it uses. `MobType` already stores the model, so instead of having to make a new class for EVERY NPC, we can just create multiple `MobType`s with different names and model IDs.

##### Do we want to extend?

> The question is: How much should we leave for the entities themselves to define, and how much do we give to `MobType`. 

Should Pathfinders be stored in `MobType`? What about InitAttributes? Mob drops? What else? The more we add, the more our Enums' constructors get bulky. 

> Perhaps some sort of builder class would work here?

We can also pretty easily override a method inside of our template class when creating a new mob from a template class to use any code we want!

```Java
(world) -> new MobTemplate(world){
    @Override
    public void runSomeCode() {
        //some code
    }
}
```

If we call `runSomeCode()` in `MobTemplate`, whatever we put there can run!

> So similarly, when should it be acceptable to do this? When should you make an entirely new class for the mob?

Another quick mention, don't extend MobTemplate with a new class for a specific mob, we can't actually differentiate between it and any other mob using MobTemplate for Pathfinders. For instance, take this pathfinder that would make a mob aggressive towards Neritantans:

```Java
this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(this, Neritantan.class, true));
```

We need to pass along a class, and if the only class we have is `MobTemplate.class`, it should generalize it to any mob for which we use that lambda expression on. (THIS IS NOT A PROBLEM FOR CLASSES EXTENDING `MobTemplate`, since those are their own classes). Anyways,

> Is there a way of specifying the mob from a template for this kind of pathfinder?

##### Associating non-extended templated mobs with entities

When we don't create a subclass of a template, we can't add a line that adds our mob ID tag, since we just use that same constructor. We can Override the `setCustomName`method, like I said a little earlier, but that means every template must have some code to get the mob type from the custom name, and a boolean variable to make sure we don't override it more than once.

Unless,

##### A template template

If every template could just share this Override, as well as some other useful code, everything would be perfect! Unfortunately, as far as I can tell, we can't have every template extend one class, because each template already extends an NMS entity. Every living entity eventually reaches the`EntityLiving` class, but we can't make a subclass of it, and extend it for our templates, since it wouldn't contain the actual vanilla entities.

So, right now I've just been using static methods with an interface to at least clean up some code, but it's not really how I want things. It contains some stuff for registering behaviours, creating the entity, and registering InitAttributes, but they require you to pass along `this` for the entity, and registering the behaviours still needs to be called by that Overridden method, just not pretty.

Anyways, this is essentially the end. Any ideas overall would be appreciated, but currently I'm trying to figure out the best way to make use of the idea of a mob template, so think about those specifically :p



# Spawning

I'll explain soon:tm:, but I can only suffer so much from revamping code...