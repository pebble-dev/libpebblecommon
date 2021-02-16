package io.rebble.libpebblecommon.util

/**
 * Common DataBuffer with bindings for each platform
 */
expect class DataBuffer {
    constructor(size: Int)
    constructor(bytes: UByteArray)

    fun putUShort(short: UShort)
    fun getUShort(): UShort

    fun putShort(short: Short)
    fun getShort(): Short

    fun putUByte(byte: UByte)
    fun getUByte(): UByte

    fun putByte(byte: Byte)
    fun getByte(): Byte

    fun putBytes(bytes: UByteArray)
    fun getBytes(count: Int): UByteArray

    fun putUInt(uint: UInt)
    fun getUInt(): UInt

    fun putInt(int: Int)
    fun getInt(): Int

    fun putULong(ulong: ULong)
    fun getULong(): ULong

    fun array(): UByteArray

    fun setEndian(endian: Char)

    fun rewind()

    /**
     * Total length of the buffer
     */
    val length: Int

    /**
     * Current position in the buffer
     */
    val readPosition: Int
}