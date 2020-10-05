package io.rebble.libpebblecommon.util

@OptIn(ExperimentalUnsignedTypes::class)
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

    fun putByte(byte: UByte)
    fun getByte(): UByte

    fun putBytes(bytes: UByteArray)
    fun getBytes(count: Int): UByteArray

    fun putUInt(uint: UInt)
    fun getUInt(): UInt

    fun putULong(ulong: ULong)
    fun getULong(): ULong

    fun array(): UByteArray

    fun setEndian(endian: Char)
}