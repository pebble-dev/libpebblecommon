package io.rebble.libpebblecommon.structmapper

import io.rebble.libpebblecommon.DataBuffer

/**
 * Represents anything mappable to a struct via a StructMapper
 */
@ExperimentalUnsignedTypes
interface Mappable {
    /**
     * Serializes/packs the mappable to its raw equivalent
     * @param endianness the endianness
     * @return The serialized mappable
     */
    fun toBytes(endianness: Char = '>'): UByteArray

    /**
     * Deserializes/unpacks raw data into the mappable
     * This will increment the seek position on the DataBuffer
     * @param bytes the data to read, seek position is incremented
     */
    fun fromBytes(bytes: DataBuffer)
}

/**
 * Represents a property mappable to a struct via a StructMapper
 */
@ExperimentalUnsignedTypes
open class StructElement<T>(private val putType: (DataBuffer, T) -> Unit, private val getType: (DataBuffer) -> T,
                            mapper: StructMapper, private val size: Int, default: T): Mappable {
    private val mapIndex = mapper.register(this)
    private var value: T = default

    fun get(): T {
        return value
    }

    fun set(value: T) {
        this.value = value
    }

    override fun toBytes(endianness: Char): UByteArray {
        val buf = DataBuffer(size)
        buf.setEndian(endianness)
        putType(buf, value)
        return buf.array()
    }

    override fun fromBytes(bytes: DataBuffer) {
        value = getType(bytes)
    }
}

@ExperimentalUnsignedTypes
class SByte     (mapper: StructMapper, default: UByte = 0u):
                StructElement<UByte>({buf,value -> buf.putByte(value)}, {buf -> buf.getByte()}, mapper, UByte.SIZE_BYTES, default)
@ExperimentalUnsignedTypes
class SUInt     (mapper: StructMapper, default: UInt = 0u):
                StructElement<UInt>({buf,value -> buf.putUInt(value)}, {buf -> buf.getUInt()}, mapper, UInt.SIZE_BYTES, default)
@ExperimentalUnsignedTypes
class SULong    (mapper: StructMapper, default: ULong = 0u):
                StructElement<ULong>({buf,value -> buf.putULong(value)}, {buf -> buf.getULong()}, mapper, ULong.SIZE_BYTES, default)
@ExperimentalUnsignedTypes
class SUShort   (mapper: StructMapper, default: UShort = 0u):
                StructElement<UShort>({buf,value -> buf.putUShort(value)}, {buf -> buf.getUShort()}, mapper, UShort.SIZE_BYTES, default)
