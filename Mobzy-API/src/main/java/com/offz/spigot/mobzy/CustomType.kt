package com.offz.spigot.mobzy

import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import com.offz.spigot.mobzy.mobs.MobTemplate
import com.offz.spigot.mobzy.mobs.MobTemplate.Companion.deserialize
import com.offz.spigot.mobzy.mobs.behaviours.AfterSpawnBehaviour
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Location
import org.bukkit.configuration.MemorySection
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent

open class CustomType {
    companion object {
        //this is used for getting a MobType from a String, which makes it easier to access from MobBuilder
        val types: MutableMap<String, EntityTypes<*>> = mutableMapOf()
        private val templateNames: MutableMap<String, String> = mutableMapOf()
        private val templates: MutableMap<String, MobTemplate> = mutableMapOf()

        @JvmStatic
        fun toEntityTypeID(name: String): String {
            return name.toLowerCase().replace(" ", "_")
        }

        @JvmStatic
        fun getType(tags: Set<String>): EntityTypes<*> =
                types[toEntityTypeID(tags.first { types.containsKey(toEntityTypeID(it)) })] ?: error("No type found for $tags registered types: $types")

        @JvmStatic
        fun getType(name: String): EntityTypes<*> {
            return types[toEntityTypeID(name)] ?: error("Mob type $name not found")
        }

        @JvmStatic
        fun getTemplate(name: String): MobTemplate {
            return templates[toEntityTypeID(name)] ?: error("Template for $name not found")
        }

        @JvmStatic
        fun getTemplate(types: EntityTypes<*>) = getTemplate(getMobNameForEntityTypes(types))

        @JvmStatic
        fun getType(builder: MobTemplate): EntityTypes<*>? {
            return types[toEntityTypeID(builder.name)]
        }

        @JvmStatic
        fun registerEntity(name: String, type: EnumCreatureType, templateName: String = name , width: Float = 1f, height: Float = 2f, func: (World) -> Entity): EntityTypes<*> {
            val injected: EntityTypes<*> = injectNewEntity(name, "zombie", bToa(EntityTypes.b { _, world -> func(world)}, type).c().a(width, height))
            types[name] = injected
            templateNames[name] = templateName
            return injected
        }

        @JvmStatic
        fun getMobNameForEntityTypes(type: EntityTypes<*>) = type.name.removePrefix("entity.minecraft.")

        @JvmStatic
        fun registerTypes() {
            logInfo("Registering types")
            for (type in types.values) {
                val name = getMobNameForEntityTypes(type)
                templates[name] = readBuilderConfig(templateNames[name]!!)
            }
            logGood("Registered: ${types.keys}")
        }

        @JvmStatic
        protected fun readBuilderConfig(name: String): MobTemplate {
            val mobCfg = mobzy.mobzyConfig.mobCfgs.values.firstOrNull { it.contains(name) }
                    ?: error("$name's builder not found")
            return deserialize((mobCfg[name] as MemorySection).getValues(true), name)
        }

        private fun bToa(b: EntityTypes.b<Entity>, creatureType: EnumCreatureType ): EntityTypes.a<Entity> = EntityTypes.a.a(b, creatureType)

        //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
        private fun injectNewEntity(name: String, extend_from: String, a: EntityTypes.a<Entity>): EntityTypes<Entity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
            val dataTypes = DataConverterRegistry.a()
                    .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().worldVersion))
                    .findChoiceType(DataConverterTypes.ENTITY).types() as MutableMap<String, Type<*>>
            if (dataTypes.containsKey("minecraft:$name")) logWarn("ALREADY CONTAINS KEY: $name")
            dataTypes["minecraft:$name"] = dataTypes["minecraft:$extend_from"]!!

            return IRegistry.a(IRegistry.ENTITY_TYPE, name, a.a(name))
        }

        @JvmStatic
        fun spawnEntity(name: String, loc: Location): org.bukkit.entity.Entity? {
            val entityTypes = getType(name)
            return spawnEntity(entityTypes, loc)
        }

        /**
         * Spawns entity at specified Location
         *
         * @param entityTypes type of entity to spawn
         * @param loc         Location to spawn at
         * @return Reference to the spawned bukkit Entity
         */
        @JvmStatic
        fun spawnEntity(entityTypes: EntityTypes<*>, loc: Location): org.bukkit.entity.Entity? {
            val nmsEntity = entityTypes.spawnCreature( // NMS method to spawn an entity from an EntityTypes
                    (loc.world as CraftWorld?)!!.handle,  // reference to the NMS world
                    null,  // EntityTag NBT compound
                    null,  // custom name of entity
                    null,  // player reference. used to know if player is OP to apply EntityTag NBT compound
                    BlockPosition(loc.blockX, loc.blockY, loc.blockZ),  // the BlockPosition to spawn at
                    EnumMobSpawn.NATURAL,
                    false,
                    false,
                    CreatureSpawnEvent.SpawnReason.CUSTOM) // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP
            //Call a method after the entity has been spawned and things like location have been determined
            if (nmsEntity is AfterSpawnBehaviour) (nmsEntity as AfterSpawnBehaviour).afterSpawn()
            //testing the enoughSpace method
//        if (!MobSpawn.enoughSpace(loc, nmsEntity.width, nmsEntity.length))
//            nmsEntity.die();
            return nmsEntity?.bukkitEntity // convert to a Bukkit entity
        }
    }
}