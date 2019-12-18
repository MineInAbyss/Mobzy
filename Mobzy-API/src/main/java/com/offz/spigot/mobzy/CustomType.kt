package com.offz.spigot.mobzy

import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import com.offz.spigot.mobzy.mobs.MobTemplate
import com.offz.spigot.mobzy.mobs.MobTemplate.Companion.deserialize
import com.offz.spigot.mobzy.mobs.behaviours.AfterSpawnBehaviour
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.MemorySection
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent


open class CustomType {
    companion object {
        //this is used for getting a MobType from a String, which makes it easier to access from MobBuilder
        val types: MutableMap<String, EntityTypes<*>> = HashMap()
        private val builders: MutableMap<String, MobTemplate?> = HashMap()
        private val plugin = Mobzy.getInstance()

        @JvmStatic
        fun toEntityTypeID(name: String): String {
            return name.toLowerCase().replace(" ", "")
        }

        @JvmStatic
        fun getType(tags: Set<String>): EntityTypes<*>? {
            for (tag in tags) if (types.containsKey(toEntityTypeID(tag))) return types[toEntityTypeID(tag)]
            return null
        }

        @JvmStatic
        fun getType(name: String): EntityTypes<*> {
            return types[toEntityTypeID(name)] ?: error("Mob type $name not found")
        }

        @JvmStatic
        fun getTemplate(name: String): MobTemplate {
            return builders[toEntityTypeID(name)] ?: error("Template for $$name not found")
        }

        @JvmStatic
        fun getTemplate(types: EntityTypes<*>) = getTemplate(getMobNameForEntityTypes(types))

        @JvmStatic
        fun getType(builder: MobTemplate): EntityTypes<*>? {
            return types[toEntityTypeID(builder.name)]
        }

        @JvmStatic
        fun registerEntity(name: String, b: EntityTypes.b<Entity>): EntityTypes<*> {
            return registerEntity(name, bToa(b))
        }

        @JvmStatic
        fun registerEntity(name: String, a: EntityTypes.a<Entity>): EntityTypes<*> {
            val injected: EntityTypes<*> = injectNewEntity(name, "zombie", a)
            types[name] = injected

            return injected
        }
        /*fun <T : Entity?> register(name: String, entitytypes_a: EntityTypes.a<*>): EntityTypes<T>? {

        }*/

        //fixme this might not work right for NPCs, could be getting the wrong thing!
        @JvmStatic
        fun getMobNameForEntityTypes(type: EntityTypes<*>) = type.f().removePrefix("entity.minecraft.")

        @JvmStatic
        fun registerTypes() {
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE.toString() + "Registering types")
            for (type in types.values) {
                val className = getMobNameForEntityTypes(type)
                val name = toEntityTypeID(className) //fixme don't think we need toEntityTypeID anymore
                builders[name] = readBuilderConfig(className)
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE.toString() + types.keys.toString())
        }

        @JvmStatic
        protected fun readBuilderConfig(name: String): MobTemplate? {
            debug("reading builder config with $name") //fixme not all the names are lowercase in config
            val mobCfg = plugin.mobzyConfig.mobCfgs.values.first { it.contains(name) }
            return deserialize((mobCfg[name] as MemorySection).getValues(true), name)
        }

        private fun bToa(b: EntityTypes.b<Entity>): EntityTypes.a<Entity> = EntityTypes.a.a(b, EnumCreatureType.MISC)

        //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
        private fun injectNewEntity(name: String, extend_from: String, a: EntityTypes.a<Entity>): EntityTypes<Entity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
            val dataTypes = DataConverterRegistry.a()
                    .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().worldVersion))
                    .findChoiceType(DataConverterTypes.ENTITY).types() as MutableMap<String, Type<*>>
            if (dataTypes.containsKey("minecraft:$name")) debug(ChatColor.YELLOW.toString() + "ALREADY CONTAINS KEY: " + name)
            dataTypes["minecraft:$name"] = dataTypes["minecraft:$extend_from"]!!

            return IRegistry.a(IRegistry.ENTITY_TYPE, name, a.a(name))
        }

        @JvmStatic
        fun spawnEntity(name: String, loc: Location): org.bukkit.entity.Entity? {
            val entityTypes = getType(name)
            return entityTypes.let { spawnEntity(it, loc) }
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