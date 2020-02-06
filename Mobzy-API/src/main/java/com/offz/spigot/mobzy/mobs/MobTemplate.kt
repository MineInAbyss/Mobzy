package com.offz.spigot.mobzy.mobs

import com.mineinabyss.idofront.items.damage
import com.mineinabyss.idofront.items.editItemMeta
import me.libraryaddict.disguise.disguisetypes.DisguiseType
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * TODO make this into a proper immutable builder.
 * A class which stores information on mobs that can be deserialized from the config.
 */
data class MobTemplate(var name: String,
                       var modelID: Int,
                       var modelMaterial: Material = Material.DIAMOND_SWORD,
                       var disguiseAs: DisguiseType = DisguiseType.ZOMBIE,
                       var temptItems: List<Material>? = null,
                       var maxHealth: Double? = null,
                       var movementSpeed: Double? = null,
                       var followRange: Double? = null,
                       var attackDamage: Double? = null,
                       var minExp: Int? = null,
                       var maxExp: Int? = minExp,
                       var isAdult: Boolean = true,
                       var deathCommands: List<String> = ArrayList(),
                       var drops: List<MobDrop> = ArrayList()) : ConfigurationSerializable {

    fun chooseDrops(): List<ItemStack?> = drops.toList().map { it.chooseDrop() }

    val modelItemStack
        get() = ItemStack(modelMaterial, 1).editItemMeta {
            it.damage = modelID
            it.isUnbreakable = true
            it.setDisplayName(name)
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
        @Suppress("UNCHECKED_CAST")
        fun deserialize(args: Map<String?, Any?>, name: String): MobTemplate {
            fun setArg(name: String, setValue: (Any) -> Unit) {
                if (args.containsKey(name))
                    setValue(args[name] ?: error("Failed to parse argument while serializing MobTemplate"))
            }

            val configName = if (args.containsKey("name")) args["name"] as String else name

            val template = MobTemplate(configName, args["model"] as Int)
            setArg("adult") { template.isAdult = it as Boolean }
            setArg("disguise-as") { template.disguiseAs = DisguiseType.valueOf(it as String) }
            setArg("drops") { drops ->
                template.drops = (drops as List<Map<String, Any>>)
                        .map { MobDrop.deserialize(it) }
                        .toList().requireNoNulls()
            }
            setArg("model-material") { template.modelMaterial = Material.getMaterial((it as String))!! }
            setArg("tempt-items") { temptItems ->
                template.temptItems = (temptItems as List<Any?>?)!!
                        .map { Material.getMaterial((it as String)) }
                        .toList().requireNoNulls()
            }
            setArg("max-health") { template.maxHealth = (it as Number).toDouble() }
            setArg("movement-speed") { template.movementSpeed = (it as Number).toDouble() }
            setArg("attack-damage") { template.attackDamage = (it as Number).toDouble() }
            setArg("follow-range") { template.followRange = (it as Number).toDouble() }
            setArg("min-exp") {
                template.minExp = (it as Number).toInt()
                template.maxExp = it.toInt() //make max not null for the null check on exp drop to proceed
            }
            setArg("max-exp") { template.maxExp = (it as Number).toInt() }
            setArg("on-death") { template.deathCommands = it as List<String> }
            return template
        }
    }

}