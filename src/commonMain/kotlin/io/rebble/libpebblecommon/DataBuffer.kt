package io.rebble.libpebblecommon

@ExperimentalUnsignedTypes
expect class DataBuffer {
    constructor(size: Int)
    constructor(bytes: UByteArray)

    fun putUShort(short: UShort)
    fun getUShort(): UShort

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