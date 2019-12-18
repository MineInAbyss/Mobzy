/*
package com.offz.spigot.abyssalcreatures.mobs.passive

import com.offz.spigot.mobzy.mobs.MobTemplate
import com.offz.spigot.mobzy.mobs.types.PassiveMob
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalLookAtPlayerPitchLock
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Material
import org.bukkit.entity.LivingEntity

class NPC(world: World?, name: String?, modelID: Int) :
        PassiveMob(world, MobTemplate(name = name!!, modelID = modelID, modelMaterial = Material.DIAMOND_AXE)) {
    //Stop from being pushed around
    override fun collide(entity: Entity) {}

    override fun move(enummovetype: EnumMoveType, d0: Double, d1: Double, d2: Double) {}
    */
/**
     * Prevent NPCs from getting damaged by anything
     *//*

    override fun damageEntity(damagesource: DamageSource, f: Float): Boolean {
        return false
    }

    override fun createPathfinders() {
        goalSelector.a(2, PathfinderGoalRandomLookaround(this))
        goalSelector.a(7, PathfinderGoalLookAtPlayerPitchLock(this, EntityTypes.PLAYER, 6.0f, 0.02f))
    }

    init {
        getBukkitEntity().customName = name
        customNameVisible = true
        setInvulnerable(true)
        setSize(0.6f, 0.6f)
        addScoreboardTag("npc")
        (getBukkitEntity() as LivingEntity).removeWhenFarAway = false
    }
}*/
