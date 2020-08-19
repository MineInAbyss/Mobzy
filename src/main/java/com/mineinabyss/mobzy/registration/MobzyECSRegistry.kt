package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.configuration.MobTypeConfigs
import com.mineinabyss.mobzy.ecs.components.Model
import com.mineinabyss.mobzy.ecs.components.Pathfinders
import com.mineinabyss.mobzy.ecs.components.minecraft.DeathLoot
import com.mineinabyss.mobzy.ecs.components.minecraft.MobAttributes
import com.mineinabyss.mobzy.ecs.components.minecraft.MobComponent
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
import com.mineinabyss.mobzy.ecs.pathfinders.TemptBehavior
import com.mineinabyss.mobzy.ecs.systems.TemptSystem
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

internal object MobzyECSRegistry : Listener {
    @EventHandler
    fun attachPathfindersOnEntityCreatedEvent(event: EntityCreatedEvent) {
        val mob = Engine.get<MobComponent>(event.id)?.mob ?: return
        val pathfinders = Engine.get<Pathfinders>(event.id)?.pathfinders ?: return
        pathfinders.forEach { (priority, component) ->
            mob.toNMS().addPathfinderGoal(priority, component.createPathfinder(mob))
            Engine.addComponent(event.id, component)
        }
    }

    fun register() {
        registerSystems()
        registerComponentSerialization()
    }

    private fun registerSystems() {
        Engine.addSystems(
                WalkingAnimationSystem, TemptSystem
        )
    }

    private fun registerComponentSerialization() {
        //TODO annotate serializable components to register this automatically
        MobTypeConfigs.addSerializerModule(SerializersModule {
            polymorphic(MobzyComponent::class) {
                subclass(MobAttributes::class, MobAttributes.serializer())
                subclass(DeathLoot::class, DeathLoot.serializer())
                subclass(Model::class, Model.serializer())
                subclass(TemptBehavior::class, TemptBehavior.serializer())
                subclass(Pathfinders::class, Pathfinders.serializer())
            }
        })
    }
}