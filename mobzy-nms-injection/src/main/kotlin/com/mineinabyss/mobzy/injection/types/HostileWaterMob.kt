package com.mineinabyss.mobzy.injection.types

import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.injection.CustomEntity
import com.mineinabyss.mobzy.injection.makeSound
import com.mineinabyss.mobzy.injection.toGeary
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

context(GearyMCContext)
class HostileWaterMob(
    type: NMSEntityType<out Axolotl>, world: NMSWorld
) : Axolotl(type, world), CustomEntity {
    override fun getBucketItemStack(): ItemStack = ItemStack(Items.WATER_BUCKET)
//    init {
//        a(PathType.i, 0.0F)
//        bM = SmoothSwimmingMoveControl(this, 85, 10, 0.5f, 0.5f, false)
//        bL = SmoothSwimmingLookControl(this, 20)
//    }

    override fun getTypeName(): Component =
        TextComponent(type.descriptionId.split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) })

    override fun getSoundVolume(): Float = toGeary().get<Sounds>()?.volume ?: super.getSoundVolume()
    override fun getAmbientSound(): SoundEvent? = makeSound(super.getAmbientSound()) { ambient }
    override fun getDeathSound(): SoundEvent? = makeSound(super.getDeathSound()) { death }
    override fun getSwimSound(): SoundEvent? = makeSound(super.getSwimSplashSound()) { swim }
    override fun getSwimSplashSound(): SoundEvent? = makeSound(super.getSwimSplashSound()) { splash }
    override fun getHurtSound(source: DamageSource): SoundEvent? = makeSound(super.getHurtSound(source)) { hurt }

}
