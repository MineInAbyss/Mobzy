package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.configuration.MobTypeConfigs
import com.mineinabyss.mobzy.ecs.components.*
import com.mineinabyss.mobzy.ecs.components.Model
import com.mineinabyss.mobzy.ecs.components.Pathfinders
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
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
            polymorphic(MobzyComponent::class) {
                subclass(Model::class, Model.serializer())
                subclass(Pathfinders::class, Pathfinders.serializer())
                subclass(Equipment::class, Equipment.serializer())
                subclass(IncreasedWaterSpeed::class, IncreasedWaterSpeed.serializer())
                subclass(Sounds::class, Sounds.serializer())

                subclass(MobAttributes::class, MobAttributes.serializer())
                subclass(DeathLoot::class, DeathLoot.serializer())
                subclass(Rideable::class, Rideable.serializer())
            }
            polymorphic(PathfinderComponent::class) {
                subclass(TemptBehavior::class, TemptBehavior.serializer())
                subclass(AvoidPlayerBehavior::class, AvoidPlayerBehavior.serializer())
                subclass(LeapAtTargetBehavior::class, LeapAtTargetBehavior.serializer())
                subclass(MeleeAttackBehavior::class, MeleeAttackBehavior.serializer())

                subclass(TargetAttacker::class, TargetAttacker.serializer())
                subclass(ThrowItemsBehavior::class, ThrowItemsBehavior.serializer())

                subclass(DiveOnTargetBehavior::class, DiveOnTargetBehavior.serializer())
                subclass(FlyDamageTargetBehavior::class, FlyDamageTargetBehavior.serializer())
                subclass(FlyTowardsTargetBehavior::class, FlyTowardsTargetBehavior.serializer())
                subclass(IdleFlyAboveGroundBehavior::class, IdleFlyAboveGroundBehavior.serializer())
                subclass(IdleFlyBehavior::class, IdleFlyBehavior.serializer())

                subclass(TargetNearbyPlayer::class, TargetNearbyPlayer.serializer())
                subclass(TargetDamager::class, TargetDamager.serializer())
                subclass(TargetAttacker::class, TargetAttacker.serializer())
            }
        })
    }
}