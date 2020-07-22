@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.mobs.MobTemplate
import com.mineinabyss.mobzy.registration.MobzyTemplates
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.registration.spawnEntity
import net.minecraft.server.v1_16_R1.Entity
import net.minecraft.server.v1_16_R1.EntityTypes
import net.minecraft.server.v1_16_R1.EnumCreatureType
import net.minecraft.server.v1_16_R1.World
import org.bukkit.Location

fun Location.spawnEntity(name: String) = spawnEntity(MobzyTypes[name])

fun Location.spawnEntity(type: EntityTypes<*>) = spawnEntity(type, this)

fun registerHardCodedTemplate(mob: String, template: MobTemplate): MobTemplate =
        MobzyTemplates.registerHardCodedTemplate(mob, template).let { template }

fun registerEntity(name: String,
                   type: EnumCreatureType,
                   width: Float = 1f,
                   height: Float = 2f,
                   func: (World) -> Entity) =
        MobzyTypes.registerEntity(name, type, width, height, func)