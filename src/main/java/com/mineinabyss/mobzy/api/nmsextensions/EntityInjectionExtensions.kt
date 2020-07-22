package com.mineinabyss.mobzy.api.nmsextensions

import net.minecraft.server.v1_16_R1.Entity
import net.minecraft.server.v1_16_R1.EntityTypes
import net.minecraft.server.v1_16_R1.IRegistry

typealias Registry<T> = IRegistry<T>

fun registerEntityType(name: String, type: EntityTypes<Entity>) = Registry.a(Registry.ENTITY_TYPE, name, type)

fun <T : Entity> EntityTypes.Builder<T>.build(name: String): EntityTypes<T> = a(name)
