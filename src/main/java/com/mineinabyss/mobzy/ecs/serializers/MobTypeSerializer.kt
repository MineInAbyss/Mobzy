package com.mineinabyss.mobzy.ecs.serializers

import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobzyTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MobTypeSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("template", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val entityName = decoder.decodeString()
        return entityName
    }

    override fun serialize(encoder: Encoder, value: String) {
        //encoder.encodeString(MobzyTypes.getNameForTemplate(value))
        encoder.encodeString(value) //TODO: Is it possible to serialize an actual mobtype and deserialize into a string?
    }

}