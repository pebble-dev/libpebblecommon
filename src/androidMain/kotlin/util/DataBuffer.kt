package io.rebble.libpebblecommon.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class DataBuffer {
    private val actualBuf: ByteBuffer

    actual constructor(size: Int) {
        actualBuf = ByteBuffer.allocate(size)
    }

    actual constructor(bytes: UByteArray) {
        actualBuf = ByteBuffer.wrap(bytes.toByteArray())
    }

    /**
     * Total length of the buffer
     */
    actual val length: Int
        get() = actualBuf.capacity()

    /**
     * Current position in the buffer
     */
    actual val readPosition: Int
        get() = actualBuf.position()

    actual val remaining: Int
        get() = actualBuf.remaining()

    actual fun putUShort(short: UShort) {
        actualBuf.putShort(short.toShort())
    }

    actual fun getUShort(): UShort = actualBuf.short.toUShort()

    actual fun putShort(short: Short) {
        actualBuf.putShort(short)
    }

    actual fun getShort(): Short = actualBuf.short

    actual fun putUByte(byte: UByte) {
        actualBuf.put(byte.toByte())
    }
    actual fun getUByte(): UByte = actualBuf.get().toUByte()

    actual fun putByte(byte: Byte) {
        actualBuf.put(byte)
    }
    actual fun getByte(): Byte = actualBuf.get()

    actual fun putBytes(bytes: UByteArray) {
        actualBuf.put(bytes.toByteArray())
    }
    actual fun getBytes(count: Int): UByteArray {
        val tBuf = ByteArray(count)
        actualBuf.get(tBuf)
        return tBuf.toUByteArray()
    }

    actual fun array(): UByteArray = actualBuf.array().toUByteArray()

    actual fun setEndian(endian: Endian) {
        when (endian) {
            Endian.Big -> actualBuf.order(ByteOrder.BIG_ENDIAN)
            Endian.Little -> actualBuf.order(ByteOrder.LITTLE_ENDIAN)
            else -> {}
        }
    }

    actual fun putInt(int: Int) {
        actualBuf.putInt(int)
    }
    actual fun getInt(): Int = actualBuf.int

    actual fun putUInt(uint: UInt) {
        actualBuf.putInt(uint.toInt())
    }
    actual fun getUInt(): UInt = actualBuf.int.toUInt()

    actual fun putULong(ulong: ULong) {
        actualBuf.putLong(ulong.toLong())
    }
    actual fun getULong(): ULong = actualBuf.long.toULong()

    actual fun rewind() {
        actualBuf.rewind()
    }
}