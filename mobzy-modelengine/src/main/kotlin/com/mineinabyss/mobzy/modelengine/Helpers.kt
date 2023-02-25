package com.mineinabyss.mobzy.modelengine

import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ModeledEntity

fun BukkitEntity.toModelEntity(): ModeledEntity? = ModelEngineAPI.getModeledEntity(uniqueId)
