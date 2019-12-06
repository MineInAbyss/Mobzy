package com.offz.spigot.mobzy.Builders

import com.offz.spigot.mobzy.Mobs.MobDrop
import me.libraryaddict.disguise.disguisetypes.DisguiseType
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.*

/**
 * TODO make this into a proper immutable builder.
 * A class which stores information on mobs that can be deserialized from the config.
 */
data class MobBuilder(var name: String,
                      var modelID: Int,
                      var modelMaterial: Material = Material.DIAMOND_SWORD,
                      var disguiseAs: DisguiseType = DisguiseType.ZOMBIE,
                      var temptItems: List<Material>? = null,
                      var maxHealth: Double? = null,
                      var movementSpeed: Double? = null,
                      var followRange: Double? = null,
                      var attackDamage: Double? = null,
                      var minExp: Int? = null,
                      var maxExp: Int? = null,
                      var isAdult: Boolean = true,
                      var deathCommands: List<String> = ArrayList(),
                      var drops: List<MobDrop> = ArrayList()) : ConfigurationSerializable {
    constructor(aName: String, aModelID: Int) : this(name = aName, modelID = aModelID)

    fun chooseDrops(): List<ItemStack?> {
        val chosenDrops: MutableList<ItemStack?> = ArrayList()
        for (drop in drops) {
            val chosenDrop = drop.chooseDrop()
            chosenDrops.add(chosenDrop)
        }
        return chosenDrops
    }

    fun getModelItemStack(): ItemStack {
        val itemStack = ItemStack(modelMaterial, 1)
        val meta = itemStack.itemMeta
        (meta as Damageable?)!!.damage = modelID
        meta!!.setDisplayName(name)
        meta.isUnbreakable = true
        itemStack.itemMeta = meta
        return itemStack
    }

    /**
     * Does nothing yet
     *
     * @return a serialized version of the mob builder
     */
    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    companion object {
        fun deserialize(args: Map<String?, Any?>, name: String): MobBuilder {
            val name = if (args.containsKey("name")) args["name"] as String else name

            val builder = MobBuilder(name, args["model"] as Int)
            if (args.containsKey("adult")) builder.isAdult = args["adult"] as Boolean
            if (args.containsKey("disguise-as")) builder.disguiseAs = DisguiseType.valueOf(args["disguise-as"] as String)
            if (args.containsKey("drops")) builder.drops = (args["drops"] as List<Map<String, Any>>)
                    .map { MobDrop.deserialize(it) }
                    .toList().requireNoNulls() //TODO check back on this later
            if (args.containsKey("model-material")) builder.modelMaterial = Material.getMaterial((args["model-material"] as String))!!
            if (args.containsKey("tempt-items")) builder.temptItems = (args["tempt-items"] as List<Any?>?)!!
                    .map { Material.getMaterial((it as String)) }
                    .toList().requireNoNulls()
            if (args.containsKey("max-health")) builder.maxHealth = (args["max-health"] as Number).toDouble()
            if (args.containsKey("movement-speed")) builder.movementSpeed = (args["movement-speed"] as Number).toDouble()
            if (args.containsKey("attack-damage")) builder.attackDamage = (args["attack-damage"] as Number).toDouble()
            if (args.containsKey("follow-range")) builder.followRange = (args["follow-range"] as Number).toDouble()
            if (args.containsKey("min-exp")) builder.minExp = (args["min-exp"] as Number).toInt()
            if (args.containsKey("max-exp")) builder.maxExp = (args["max-exp"] as Number).toInt()
            if (args.containsKey("on-death")) builder.deathCommands = args["on-death"] as List<String>
            return builder
        }
    }

}