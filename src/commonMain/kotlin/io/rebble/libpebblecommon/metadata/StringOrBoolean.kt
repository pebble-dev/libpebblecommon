package io.rebble.libpebblecommon.metadata
import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

@Serializable(StringOrBoolean.Companion::class)
data class StringOrBoolean(val value: Boolean) {
    @Serializer(forClass = StringOrBoolean::class)
    companion object : KSerializer<StringOrBoolean> {
        override fun serialize(encoder: Encoder, value: StringOrBoolean) {
            encoder.encodeString(if (value.value) "true" else "false")
        }

        override fun deserialize(decoder: Decoder): StringOrBoolean {
            require(decoder is JsonDecoder)
            val element = decoder.decodeJsonElement()
            if (element.jsonPrimitive.content != "true" && element.jsonPrimitive.content != "false") {
                throw SerializationException("StringOrBoolean value is not a boolean keyword")
            }
            return StringOrBoolean(element.jsonPrimitive.content == "true")
        }
    }
}