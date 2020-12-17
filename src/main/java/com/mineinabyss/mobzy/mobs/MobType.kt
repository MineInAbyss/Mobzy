package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.registration.MobzyTypes
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.v1_16_R2.EnumCreatureType

/**
 * A class which stores information on mobs that can be deserialized from the config.
 */
@Serializable
data class MobType(
        val baseClass: String,
        val creatureType: EnumCreatureType,
        val targets: Map<Double, PathfinderComponent>? = null,
        val goals: Map<Double, PathfinderComponent>? = null
) : GearyEntityType() {
    override fun MutableSet<GearyComponent>.addStaticComponents() {
        if (targets != null || goals != null)
            add(Pathfinders(targets, goals))
    }

    @Transient
    override val types = MobzyTypes

    val nmsType: NMSEntityType<*> by lazy { MobzyTypeRegistry[name] }
}
