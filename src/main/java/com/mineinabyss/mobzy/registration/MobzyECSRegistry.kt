package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.configuration.MobTypeConfigs
import com.mineinabyss.mobzy.ecs.components.Model
import com.mineinabyss.mobzy.ecs.components.Pathfinders
import com.mineinabyss.mobzy.ecs.components.minecraft.DeathLoot
import com.mineinabyss.mobzy.ecs.components.minecraft.MobAttributes
import com.mineinabyss.mobzy.ecs.components.minecraft.MobComponent
import com.mineinabyss.mobzy.ecs.components.*
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
import com.mineinabyss.mobzy.ecs.pathfinders.TemptBehavior
import com.mineinabyss.mobzy.ecs.systems.TemptSystem
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
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

internal object MobzyECSRegistry : Listener {
    @EventHandler
    fun attachPathfindersOnEntityCreatedEvent(event: EntityCreatedEvent) {
        val mob = Engine.get<MobComponent>(event.id)?.mob ?: return
        val (targets, goals) = Engine.get<Pathfinders>(event.id) ?: return

        targets?.forEach { (priority, component) ->
            mob.toNMS().addTargetSelector(priority.toInt(), component.build(mob))
            Engine.addComponent(event.id, component)
        }
        goals?.forEach { (priority, component) ->
            mob.toNMS().addPathfinderGoal(priority.toInt(), component.build(mob))
            Engine.addComponent(event.id, component)
        }
    }

    fun register() {
        registerSystems()
        registerComponentSerialization()
    }

    private fun registerSystems() {
        Engine.addSystems(
                WalkingAnimationSystem
        )
    }

    private fun registerComponentSerialization() {
        //TODO annotate serializable components to register this automatically
        MobTypeConfigs.addSerializerModule(SerializersModule {
            polymorphic<MobzyComponent> {
                subclass<Model>()
                subclass<Pathfinders>()
                subclass<Equipment>()
                subclass<IncreasedWaterSpeed>()
                subclass<Sounds>()

                subclass<MobAttributes>()
                subclass<DeathLoot>()
                subclass<Rideable>()
            }
            polymorphic<PathfinderComponent> {
                subclass<TemptBehavior>()
                subclass<AvoidPlayerBehavior>()
                subclass<LeapAtTargetBehavior>()
                subclass<MeleeAttackBehavior>()

                subclass<TargetAttacker>()
                subclass<ThrowItemsBehavior>()

                subclass<DiveOnTargetBehavior>()
                subclass<FlyDamageTargetBehavior>()
                subclass<FlyTowardsTargetBehavior>()
                subclass<IdleFlyAboveGroundBehavior>()
                subclass<IdleFlyBehavior>()

                //TODO move into TargetComponent
                subclass<TargetNearbyPlayer>()
                subclass<TargetDamager>()
                subclass<TargetAttacker>()
            }
        })
    }
}