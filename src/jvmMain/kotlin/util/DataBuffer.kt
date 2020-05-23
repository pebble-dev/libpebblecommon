package io.rebble.libpebblecommon.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

@ExperimentalUnsignedTypes
actual class DataBuffer {
    private val actualBuf: ByteBuffer

    actual constructor(size: Int) {
        actualBuf = ByteBuffer.allocate(size)
    }
    actual constructor(bytes: UByteArray) {
        actualBuf = ByteBuffer.wrap(bytes.toByteArray())
    }

    actual fun putUShort(short: UShort) {
        actualBuf.putShort(short.toShort())
    }
    actual fun getUShort(): UShort = actualBuf.short.toUShort()

    actual fun putShort(short: Short) {
        actualBuf.putShort(short)
    }
    actual fun getShort(): Short = actualBuf.short

    actual fun putByte(byte: UByte) {
        actualBuf.put(byte.toByte())
    }
    actual fun getByte(): UByte = actualBuf.get().toUByte()

    actual fun putBytes(bytes: UByteArray) {
        actualBuf.put(bytes.toByteArray())
    }
    actual fun getBytes(count: Int): UByteArray {
        val tBuf = ByteArray(count)
        actualBuf.get(tBuf)
        return tBuf.toUByteArray()
    }

    actual fun array(): UByteArray = actualBuf.array().toUByteArray()

    actual fun setEndian(endian: Char) {
        when (endian) {
            '>' -> actualBuf.order(ByteOrder.BIG_ENDIAN)
            '<' -> actualBuf.order(ByteOrder.LITTLE_ENDIAN)
        }
    }

    actual fun putUInt(uint: UInt) {
        actualBuf.putInt(uint.toInt())
    }
    actual fun getUInt(): UInt = actualBuf.int.toUInt()

    actual fun putULong(ulong: ULong) {
        actualBuf.putLong(ulong.toLong())
    }
    actual fun getULong(): ULong = actualBuf.long.toULong()
}