package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:packet_entity")
@AutoscanComponent
data class PacketEntity(
    @SerialName("type_id") val typeId: Int
)