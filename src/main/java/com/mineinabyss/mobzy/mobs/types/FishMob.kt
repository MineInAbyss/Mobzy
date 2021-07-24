package com.mineinabyss.mobzy.mobs.types

import com.mieninabyss.mobzy.processor.GenerateFromBase
import com.mineinabyss.idofront.nms.aliases.*
import net.minecraft.world.entity.animal.EntityFishSchool

@GenerateFromBase(base = MobBase::class, createFor = [EntityFishSchool::class])
open class FishMob(type: NMSEntityType<*>, world: NMSWorld) : MobzyEntityFishSchool(world, type) {
    //bucket you get from picking up fish (we disable this interaction anyways)
    override fun getBucketItem(): NMSItemStack =
        NMSItemStack(NMSItems.nX) //Water Bucket TODO let protocolburrito wrap this kinda stuff

    //can't be null so it's harder to make this configurable
    override fun getSoundFlop(): NMSSound = NMSSounds.cY

    //on player interact
    override fun b(entityhuman: NMSEntityHuman, enumhand: NMSHand) = NMSInteractionResult.d //PASS

    init {
        //TODO dont add these scoreboard tags, I think they're only used to filter by type when spawning, just use a
        // better system than this...
        addScoreboardTag("fishMob")
//        entity.removeWhenFarAway = true
    }
}
