package io.rebble.libpebblecommon.metadata
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class StringOrBoolean(val value: Boolean) {
    @Serializer(forClass = StringOrBoolean::class)
    companion object : KSerializer<StringOrBoolean> {
        override fun serialize(encoder: Encoder, value: StringOrBoolean) {
            encoder.encodeString(if (value.value) "true" else "false")
        }

        override fun deserialize(decoder: Decoder): StringOrBoolean = try {
            StringOrBoolean(decoder.decodeString() == "true")
        } catch (e: Error) {
            StringOrBoolean(decoder.decodeBoolean())
        }
    }
}