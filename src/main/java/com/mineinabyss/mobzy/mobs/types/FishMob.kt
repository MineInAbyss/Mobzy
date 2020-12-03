package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.mobzy.api.nms.aliases.*
import net.minecraft.server.v1_16_R2.EntityFishSchool
import org.bukkit.entity.HumanEntity

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
@GenerateFromBase(base = MobBase::class, createFor = [EntityFishSchool::class])
open class FishMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityFishSchool(world, type) {
    //bucket you get from picking up fish (we disable this interaction anyways)
    override fun eK(): NMSItemStack = NMSItemStack(NMSItems.WATER_BUCKET)

    //can't be null so it's harder to make this configurable
    override fun getSoundFlop(): NMSSound = NMSSounds.ENTITY_COD_FLOP

    override fun onPlayerInteract(player: HumanEntity, enumhand: NMSHand) = NMSInteractionResult.PASS

    init {
        initMob()
        addScoreboardTag("fishMob")
//        entity.removeWhenFarAway = true
    }
}
