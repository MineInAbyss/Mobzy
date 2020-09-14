package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.looty.ecs.components.Screaming
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.ecs.systems.ScreamingSystem
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.ecs.components.MobComponent
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.ecs.components.initialization.Equipment
import com.mineinabyss.mobzy.ecs.components.initialization.IncreasedWaterSpeed
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.ecs.events.MobLoadEvent
import com.mineinabyss.mobzy.ecs.goals.minecraft.AvoidPlayerBehavior
import com.mineinabyss.mobzy.ecs.goals.minecraft.LeapAtTargetBehavior
import com.mineinabyss.mobzy.ecs.goals.minecraft.MeleeAttackBehavior
import com.mineinabyss.mobzy.ecs.goals.minecraft.TemptBehavior
import com.mineinabyss.mobzy.ecs.goals.mobzy.flying.*
import com.mineinabyss.mobzy.ecs.goals.mobzy.hostile.ThrowItemsBehavior
import com.mineinabyss.mobzy.ecs.goals.targetselectors.TargetAttacker
import com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft.TargetDamager
import com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft.TargetNearbyPlayer
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

internal object MobzyECSRegistry : Listener {
    @EventHandler
    fun attachPathfindersOnEntityLoadedEvent(event: MobLoadEvent) {
        val (entity) = event
        val (mob) = entity.get<MobComponent>() ?: return
        val (targets, goals) = entity.get<Pathfinders>() ?: return

        targets?.forEach { (priority, component) ->
            mob.toNMS().addTargetSelector(priority.toInt(), component.build(mob))

            entity.addComponent(component)
        }
        goals?.forEach { (priority, component) ->
            mob.toNMS().addPathfinderGoal(priority.toInt(), component.build(mob))
            entity.addComponent(component)
        }
    }

    fun register() {
        registerSystems()
        registerComponentSerialization()
    }

    private fun registerSystems() {
        Engine.addSystems(
                WalkingAnimationSystem,
                ItemTrackerSystem,
                ScreamingSystem
        )
    }

    private fun registerComponentSerialization() {
        //TODO annotate serializable components to register this automatically
        Formats.addSerializerModule(SerializersModule {
            polymorphic(MobzyComponent::class) {
                subclass(Model.serializer())
                subclass(Pathfinders.serializer())
                subclass(Equipment.serializer())
                subclass(IncreasedWaterSpeed.serializer())
                subclass(Sounds.serializer())

                subclass(MobAttributes.serializer())
                subclass(DeathLoot.serializer())
                subclass(Rideable.serializer())

                subclass(Screaming.serializer())
            }
            polymorphic(PathfinderComponent::class) {
                subclass(TemptBehavior.serializer())
                subclass(AvoidPlayerBehavior.serializer())
                subclass(LeapAtTargetBehavior.serializer())
                subclass(MeleeAttackBehavior.serializer())

                subclass(TargetAttacker.serializer())
                subclass(ThrowItemsBehavior.serializer())

                subclass(DiveOnTargetBehavior.serializer())
                subclass(FlyDamageTargetBehavior.serializer())
                subclass(FlyTowardsTargetBehavior.serializer())
                subclass(IdleFlyAboveGroundBehavior.serializer())
                subclass(IdleFlyBehavior.serializer())

                subclass(TargetNearbyPlayer.serializer())
                subclass(TargetDamager.serializer())
                subclass(TargetAttacker.serializer())
            }
        })
    }
}