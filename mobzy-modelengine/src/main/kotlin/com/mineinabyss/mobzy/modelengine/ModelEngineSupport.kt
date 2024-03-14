package com.mineinabyss.mobzy.modelengine

import com.mineinabyss.geary.addons.dsl.GearyAddonWithDefault
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.plugin.service
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.modelengine.animation.AnimationController
import com.mineinabyss.mobzy.modelengine.animation.ModelEngineAnimationController
import com.mineinabyss.mobzy.modelengine.animation.ModelInteractionListener
import com.mineinabyss.mobzy.modelengine.intializers.ModelEngineWorldListener
import com.mineinabyss.mobzy.modelengine.intializers.createModelEngineListener
import com.mineinabyss.mobzy.modelengine.nametag.ModelEngineNameTagListener
import com.mineinabyss.mobzy.modelengine.riding.ModelEngineRidingListener

val mobzyModelEngine: ModelEngineSupport? by DI.observe()

interface ModelEngineSupport {
    val animationController: AnimationController

    companion object : GearyAddonWithDefault<ModelEngineSupport> {
        override fun default() = object : ModelEngineSupport {
            override val animationController = ModelEngineAnimationController()
        }

        override fun ModelEngineSupport.install(): Unit = geary.run {
            DI.add(this)

            createModelEngineListener()

            mobzy.plugin.apply {
                listeners(
                    ModelInteractionListener(),
                    ModelEngineNameTagListener(),
                    ModelEngineRidingListener(),
                    ModelEngineWorldListener(),
                )
                service<AnimationController>(ModelEngineAnimationController())
            }
        }
    }
}
