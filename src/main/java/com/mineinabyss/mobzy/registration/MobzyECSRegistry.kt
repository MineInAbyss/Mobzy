package com.mineinabyss.mobzy.registration

import com.mineinabyss.geary.dsl.attachToGeary
import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.components.StaticType
import com.mineinabyss.mobzy.Mobzy
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.ecs.components.initialization.Equipment
import com.mineinabyss.mobzy.ecs.components.initialization.IncreasedWaterSpeed
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.Pathfinders
import com.mineinabyss.mobzy.ecs.components.interaction.AttackPotionEffects
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
    attachToGeary(types = MobzyTypes) {
        systems(
                WalkingAnimationSystem
        )

        serializers {
            polymorphic(GearyComponent::class) {
                subclass(StaticType.serializer()) //TODO move into Geary

                subclass(Model.serializer())
                subclass(Pathfinders.serializer())
                subclass(Equipment.serializer())
                subclass(IncreasedWaterSpeed.serializer())
                subclass(Sounds.serializer())

                subclass(MobAttributes.serializer())
                subclass(DeathLoot.serializer())
                subclass(Rideable.serializer())

                subclass(AttackPotionEffects.serializer())
            }
            polymorphic(PathfinderComponent::class) {
                subclass(TemptBehavior.serializer())
                subclass(AvoidPlayerBehavior.serializer())
                subclass(LandStrollBehavior.serializer())
                subclass(RandomLookAroundBehavior.serializer())
                subclass(PanicOnHitBehavior.serializer())
                subclass(FollowParentBehaviour.serializer())

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
