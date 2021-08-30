package com.mineinabyss.mobzy.mobs

import com.mineinabyss.idofront.nms.aliases.NMSEntityInsentient
import org.bukkit.entity.Mob

/**
 * A class for all our custom living entities to extend. Some shared behaviour is in here, though because we often
 * need to access protected variables, we force implementation of them here. Since a lot of these things have obfuscated
 * names, we may expand upon this class to act as a wrapper interface of sorts for custom NMS mobs.
 *
 * We share how we override some functions to implement custom behaviour with the help of [MobBase] and an
 * annotation processor.
 *
 * @see MobBase
 */
interface CustomMob : CustomEntity {
    override val nmsEntity: NMSEntityInsentient
    override val entity: Mob
}
