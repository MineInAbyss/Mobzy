package com.mineinabyss.mobzy.modelengine

import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.modelengine.intializers.SetModelEngineModel
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ModeledEntity
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager
import com.ticxo.modelengine.api.model.bone.manager.MountManager
import com.ticxo.modelengine.api.model.bone.type.Mount
import java.util.*
import kotlin.jvm.optionals.getOrNull

fun BukkitEntity.toModelEntity(): ModeledEntity? = ModelEngineAPI.getModeledEntity(uniqueId)

class MountManagerWithBehavior<T: BoneBehavior>(
    mountManager: MountManager,
    behaviorManager: BehaviorManager<T>,
): MountManager by mountManager, BehaviorManager<T> by behaviorManager

fun BukkitEntity.getMountManager() : MountManagerWithBehavior<*>? {
    val mountManager = (toModelEntity()
        ?.getModel(toGearyOrNull()?.get<SetModelEngineModel>()?.modelId)
        ?.getOrNull()
        ?.getMountManager<Nothing>() as Optional<*>?)
        ?.getOrNull() ?: return null

    return MountManagerWithBehavior(
        mountManager as MountManager,
        mountManager as BehaviorManager<*>
    )
}
