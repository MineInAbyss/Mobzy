package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.CopyableComponent
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.Pathfinders
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.v1_16_R2.EnumCreatureType
import kotlin.reflect.KClass

/**
 * A class which stores information on mobs that can be deserialized from the config.
 */
@Serializable
data class MobType(
        val baseClass: String,
        val creatureType: EnumCreatureType,
        @SerialName("name") private val _name: String? = null,
        @SerialName("staticComponents") private val _staticComponents: MutableSet<MobzyComponent> = mutableSetOf(),
        @SerialName("components") private val _components: Set<CopyableComponent> = setOf(),
        val pathfinders: Map<Int, PathfinderComponent>? = null) {
    init {
        if (pathfinders != null) _staticComponents += Pathfinders(pathfinders)
    }

    val name by lazy { _name ?: MobTypes.getNameForTemplate(this) }

    @Transient
    val components: Map<KClass<out CopyableComponent>, CopyableComponent> = _components.associateBy { it::class }

    @Transient
    val staticComponents: Map<KClass<out MobzyComponent>, MobzyComponent> = _staticComponents.associateBy { it::class }
    val nmsType: NMSEntityType<*> by lazy { MobzyTypeRegistry[name] }

    inline fun <reified T : MobzyComponent> get(): T? = staticComponents[T::class] as? T

    inline fun <reified T : MobzyComponent> has() = staticComponents.containsKey(T::class)
}