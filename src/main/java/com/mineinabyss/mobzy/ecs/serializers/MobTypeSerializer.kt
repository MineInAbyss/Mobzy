package com.mineinabyss.mobzy.ecs.serializers

import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobzyTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//TODO: Maybe deserialize as a different type? If we use MobType, the deserialized MobType needs to have been
// registered before any types with references to that MobType.
object MobTypeSerializer : KSerializer<MobType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("template", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MobType {
        val entityName = decoder.decodeString()
        return MobzyTypes[entityName]
    }

    override fun serialize(encoder: Encoder, value: MobType) {
        encoder.encodeString(MobzyTypes.getNameForTemplate(value))
    }

}