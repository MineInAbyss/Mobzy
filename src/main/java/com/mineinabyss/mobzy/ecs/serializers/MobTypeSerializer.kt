package com.mineinabyss.mobzy.ecs.serializers

import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobzyTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//TODO expose the ByReferenceSerializer so we don't need a duplicate serializer for this
object MobTypeSerializer : KSerializer<MobType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("template", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MobType {
        return MobzyTypes[decoder.decodeString()]
    }

    override fun serialize(encoder: Encoder, value: MobType) {
        encoder.encodeString(value.name)
    }
}
