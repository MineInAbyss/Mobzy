package com.mineinabyss.mobzy.modelengine

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.textcomponents.serialize
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.mobzy
import com.ticxo.modelengine.api.ModelEngineAPI
import kotlinx.coroutines.yield
import org.bukkit.Color

class ModelEngineTracker : GearyListener() {
    val TargetScope.bukkit by onSet<BukkitEntity>()
    val TargetScope.model by onSet<ModelEngineComponent>()

    @Handler
    fun TargetScope.registerModelEngine() {
        mobzy.plugin.launch {
            yield() // Wait till next tick so some entity stuff gets initialized
            val modelEntity = ModelEngineAPI.getOrCreateModeledEntity(bukkit)

            val createdModel =
                ModelEngineAPI.createActiveModel(ModelEngineAPI.getBlueprint(model.modelId)).apply {
                    if (model.damageTint) rendererHandler.setColor(Color.RED)
                }

            modelEntity.apply {
                addModel(createdModel, false)
                bukkit.customName()?.let {
                    modelEntity.getModel(model.modelId).nametagHandler.bones["nametag"]?.apply {
                        customName = it.serialize()
                        isCustomNameVisible = model.nametag
                    }
                }
                setStepHeight(model.stepHeight)
                isBaseEntityVisible = !model.invisible
            }
        }
    }
}
