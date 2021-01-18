package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.minecraft.dsl.attachToGeary
import com.mineinabyss.mobzy.Mobzy
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.ecs.components.initialization.*
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.ecs.goals.minecraft.*
import com.mineinabyss.mobzy.ecs.goals.mobzy.flying.*
import com.mineinabyss.mobzy.ecs.goals.mobzy.hostile.ThrowItemsBehavior
import com.mineinabyss.mobzy.ecs.goals.targetselectors.TargetAttacker
import com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft.TargetDamager
import com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft.TargetNearbyPlayer
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun Mobzy.attachToGeary() {
    attachToGeary (types = MobzyTypes) {
        systems(
            WalkingAnimationSystem
        )

        components {
            component(Model.serializer())
            component(ItemModel.serializer())
            component(Pathfinders.serializer())
            component(Equipment.serializer())
            component(IncreasedWaterSpeed.serializer())
            component(Sounds.serializer())
            component(MobAttributes.serializer())
            component(DeathLoot.serializer())
            component(Rideable.serializer())
        }

        serializers {
            polymorphic(PathfinderComponent::class) {
                subclass(TemptBehavior.serializer())
                subclass(AvoidPlayerBehavior.serializer())
                subclass(LandStrollBehavior.serializer())
                subclass(RandomLookAroundBehavior.serializer())
                subclass(PanicOnHitBehavior.serializer())
                subclass(FollowParentBehaviour.serializer())
                subclass(FloatBehavior.serializer())

                subclass(LeapAtTargetBehavior.serializer())
                subclass(MeleeAttackBehavior.serializer())
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
        }

        bukkitEntityAccess {
            entityConversion { toMobzy() }
        }
    }
}
