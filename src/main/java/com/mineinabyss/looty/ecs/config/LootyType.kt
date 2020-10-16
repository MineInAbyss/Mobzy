package com.mineinabyss.looty.ecs.config

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.looty.ecs.components.LootyEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LootyType(
        val item: SerializableItemStack,
        private val _name: String? = null,
        private val _staticComponents: MutableSet<GearyComponent> = mutableSetOf(),
        private val _components: Set<GearyComponent> = setOf(),
): GearyEntityType() {
    @Transient
    override val types = LootyTypes

    fun instantiateItemStack() = item.toItemStack()

    override fun instantiate(): LootyEntity =
        LootyEntity(Engine.getNextId(), instantiateItemStack()).apply {
            addComponents(instantiateComponents())
        }
}