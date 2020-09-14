package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.registration.MobTypes
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.SetSerializer
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
        @SerialName("components") private val _components: Set<MobzyComponent> = setOf(),
        val targets: Map<Double, PathfinderComponent>? = null,
        val goals: Map<Double, PathfinderComponent>? = null) {
    init {
        if (targets != null || goals != null)
            _staticComponents += Pathfinders(targets, goals) //TODO GOAP
    }

    val name by lazy { _name ?: MobTypes.getNameForTemplate(this) }

    //TODO this is the safest and cleanest way to deepcopy. Check how this performs vs deepcopy's reflection method.
    val components: String by lazy { Formats.yamlFormat.encodeToString(componentSerializer, _components) }
    val staticComponents: Map<KClass<out MobzyComponent>, MobzyComponent> by lazy { _staticComponents.associateBy { it::class } }
    val nmsType: NMSEntityType<*> by lazy { MobzyTypeRegistry[name] }

    inline fun <reified T : MobzyComponent> get(): T? = staticComponents[T::class] as? T

    inline fun <reified T : MobzyComponent> has() = staticComponents.containsKey(T::class)

    fun instantiateComponents(existingComponents: Set<MobzyComponent> = emptySet()): Set<MobzyComponent> =
            Formats.yamlFormat.decodeFromString(componentSerializer, components).apply {
                forEach { it.persist = true }
            } + existingComponents + staticComponents.values

    companion object {
        private val componentSerializer = SetSerializer(PolymorphicSerializer(MobzyComponent::class))
    }
}