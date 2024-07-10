package io.rebble.libpebblecommon.util

expect fun runBlocking(block: suspend () -> Unit)

object KUtil {
    fun byteArrayAsUByteArray(arr: ByteArray): UByteArray = arr.asUByteArray()
    fun uByteArrayAsByteArray(arr: UByteArray): ByteArray = arr.asByteArray()
}

infix fun Byte.ushr(bitCount: Int): Byte = ((this.toInt()) ushr bitCount).toByte()
infix fun Byte.shl(bitCount: Int): Byte = ((this.toInt()) shl bitCount).toByte()
infix fun UByte.shr(bitCount: Int): UByte = ((this.toUInt()) shr bitCount).toUByte()
infix fun UByte.shl(bitCount: Int): UByte = ((this.toUInt()) shl bitCount).toUByte()

infix fun Short.shl(bitCount: Int): Short = ((this.toInt()) shl bitCount).toShort()
infix fun UShort.shl(bitCount: Int): UShort = ((this.toUInt()) shl bitCount).toUShort()

fun String.encodeToByteArrayTrimmed(maxBytes: Int): ByteArray {
    check(maxBytes >= 2) {
        "maxBytes must be at least 2 to fit ellipsis character. Got $maxBytes instead"
    }

    val encodedOriginal = encodeToByteArray()
    if (encodedOriginal.size <= maxBytes) {
        return encodedOriginal
    }

    var trimmedString = take(maxBytes - 1)
    var encoded = "$trimmedString…".encodeToByteArray()

    while (encoded.size > maxBytes) {
        trimmedString = trimmedString.take(trimmedString.length - 1)
        encoded = "$trimmedString…".encodeToByteArray()
    }

    return encoded
}

fun String.trimWithEllipsis(maxLength: Int): String {
    if (length <= maxLength) {
        return this
    }
    return take(maxLength - 1) + "…"
}
