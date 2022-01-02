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