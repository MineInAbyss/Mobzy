package com.mineinabyss.mobzy.mobs

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.ecs.components.SerializableComponent
import com.mineinabyss.mobzy.ecs.components.minecraft.deathLoot
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyRegistry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.v1_16_R1.EnumCreatureType
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

/**
 * A class which stores information on mobs that can be deserialized from the config.
 */
@Serializable
data class MobType(
        @SerialName("name") private val _name: String? = null,
        @SerialName("staticComponents") private val _staticComponents: Set<MobzyComponent> = setOf(),
        @SerialName("components") private val _components: Set<SerializableComponent> = setOf(),
        val creatureType: EnumCreatureType,
        val parentClass: String,
        val isAdult: Boolean = true) {
    val name by lazy { _name ?: MobTypes.getNameForTemplate(this) }

    @Transient
    val components: Map<KClass<out SerializableComponent>, SerializableComponent> = _components.associateBy { it::class }

    @Transient
    val staticComponents: Map<KClass<out MobzyComponent>, MobzyComponent> = _staticComponents.associateBy { it::class }
    val nmsType: NMSEntityType<*> get() = MobzyRegistry[name] //TODO change how this is done

    inline fun <reified T : MobzyComponent> get(): T? = staticComponents[T::class] as? T //TODO unsure if this is null-safe

    inline fun <reified T : MobzyComponent> has() = staticComponents.containsKey(T::class)

    fun chooseDrops(looting: Int = 0, fire: Int = 0): List<ItemStack?> = deathLoot?.drops?.toList()?.map { it.chooseDrop(looting, fire) } ?: listOf()
}