package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.configuration.MobTypeConfigs
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyRegistry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.v1_16_R1.EnumCreatureType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

typealias AnyMobType = MobType

/**
 * A class which stores information on mobs that can be deserialized from the config.
 */
@Serializable
data class MobType(
        @SerialName("name") private val _name: String? = null,
        @SerialName("components") private val _components: Set<MobzyComponent> = setOf(),
        val creatureType: EnumCreatureType,
        val parentClass: String,
        val model: Int,
        val modelMaterial: Material = Material.DIAMOND_SWORD,
        val isAdult: Boolean = true) {
    val name by lazy { _name ?: MobTypes.getNameForTemplate(this) }

    @Transient
    val components: Map<KClass<out MobzyComponent>, MobzyComponent> = _components.associateBy { it::class }
    val nmsType: NMSEntityType<*> get() = MobzyRegistry[name] //TODO change how this is done

    inline fun <reified T : MobzyComponent> get(): T? = components[T::class] as? T //TODO unsure if this is null-safe

    fun chooseDrops(looting: Int = 0, fire: Int = 0): List<ItemStack?> = deathLoot?.drops?.toList()?.map { it.chooseDrop(looting, fire) } ?: listOf()

    val modelItemStack
        get() = ItemStack(modelMaterial).editItemMeta {
            setCustomModelData(model)
        }
}

fun main() {
    val type = MobType(
            model = 1,
            creatureType = EnumCreatureType.CREATURE,
            parentClass = "mobzy:passive",
            _components = setOf(MobAttributes(movementSpeed = 1.0))
    )
    println(MobTypeConfigs.formatYaml.stringify(MobType.serializer(), type))
}