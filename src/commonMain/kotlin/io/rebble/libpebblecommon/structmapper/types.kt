package io.rebble.libpebblecommon.structmapper

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.bytes
import com.benasher44.uuid.uuidOf
import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.exceptions.PacketEncodeException
import io.rebble.libpebblecommon.util.DataBuffer

/**
 * Represents anything mappable to a struct via a StructMapper
 */
@OptIn(ExperimentalUnsignedTypes::class)
interface Mappable {
    /**
     * Serializes/packs the mappable to its raw equivalent
     * @param endianness the endianness
     * @return The serialized mappable
     */
    fun toBytes(): UByteArray

    /**
     * Deserializes/unpacks raw data into the mappable
     * This will increment the seek position on the DataBuffer
     * @param bytes the data to read, seek position is incremented
     */
    fun fromBytes(bytes: DataBuffer)
}

/**
 * Represents a property mappable to a struct via a StructMapper
 * @param endianness represents endianness on serialization
 */
@OptIn(ExperimentalUnsignedTypes::class)
open class StructElement<T>(
    private val putType: (DataBuffer, StructElement<T>) -> Unit,
    private val getType: (DataBuffer, StructElement<T>) -> Unit,
    mapper: StructMapper,
    size: Int,
    default: T,
    endianness: Char = '|'
) : Mappable { //TODO: Element-level endianness on deserialization
    var size = size
    private val mapIndex = mapper.register(this)
    private var value: T = default
    var isLittleEndian = endianness == '<'

    fun get(): T {
        return value
    }

    fun set(value: T, newSize: Int? = null) {
        if (newSize != null) size = newSize
        this.value = value
    }

    override fun toBytes(): UByteArray {
        if (size < 0) throw PacketDecodeException("Invalid StructElement size: $size")
        else if (size == 0) return ubyteArrayOf()
        val buf = DataBuffer(size)
        putType(buf, this)
        return if (isLittleEndian) buf.array().reversedArray() else buf.array()
    }

    override fun fromBytes(bytes: DataBuffer) {
        getType(bytes, this)
    }

    fun setEndiannes(endianness: Char) {
        isLittleEndian = endianness == '<'
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class SByte(mapper: StructMapper, default: UByte = 0u) :
    StructElement<UByte>(
        { buf, el -> buf.putByte(el.get()) },
        { buf, el -> el.set(buf.getByte()) },
        mapper,
        UByte.SIZE_BYTES,
        default
    )

@OptIn(ExperimentalUnsignedTypes::class)
class SUInt(mapper: StructMapper, default: UInt = 0u, endianness: Char = '|') :
    StructElement<UInt>(
        { buf, el -> buf.putUInt(el.get()) },
        { buf, el -> el.set(buf.getUInt()) },
        mapper,
        UInt.SIZE_BYTES,
        default,
        endianness
    )

@OptIn(ExperimentalUnsignedTypes::class)
class SULong(mapper: StructMapper, default: ULong = 0u) :
    StructElement<ULong>(
        { buf, el -> buf.putULong(el.get()) },
        { buf, el -> el.set(buf.getULong()) },
        mapper,
        ULong.SIZE_BYTES,
        default
    )

@OptIn(ExperimentalUnsignedTypes::class)
class SUShort(mapper: StructMapper, default: UShort = 0u, endianness: Char = '|') :
    StructElement<UShort>(
        { buf, el -> buf.putUShort(el.get()) },
        { buf, el -> el.set(buf.getUShort()) },
        mapper,
        UShort.SIZE_BYTES,
        default,
        endianness
    )

@OptIn(ExperimentalUnsignedTypes::class)
class SShort(mapper: StructMapper, default: Short = 0) :
    StructElement<Short>(
        { buf, el -> buf.putShort(el.get()) },
        { buf, el -> el.set(buf.getShort()) },
        mapper,
        Short.SIZE_BYTES,
        default
    )

@OptIn(ExperimentalUnsignedTypes::class)
class SUUID(mapper: StructMapper, default: Uuid = Uuid(0, 0)) :
    StructElement<Uuid>(
        { buf, el -> buf.putBytes(el.get().bytes.toUByteArray()) },
        { buf, el -> el.set(uuidOf(buf.getBytes(2 * ULong.SIZE_BYTES).toByteArray())) },
        mapper,
        2 * ULong.SIZE_BYTES,
        default
    )

/**
 * Represents a string (UTF-8) in a struct, includes framing for length
 */
@OptIn(ExperimentalUnsignedTypes::class)
@ExperimentalStdlibApi
class SString(mapper: StructMapper, default: String = "") :
    StructElement<String>(
        { buf, el ->
            buf.putByte(el.get().length.toUByte()); buf.putBytes(
            el.get().encodeToByteArray().toUByteArray()
        )
        },
        { buf, el ->
            val len = buf.getByte().toInt()
            el.set(buf.getBytes(len).toByteArray().decodeToString(), len)
        }, mapper, default.length, default
    )

/**
 * Represents arbitrary bytes in a struct
 * @param length the number of bytes, when serializing this is used to pad/truncate the provided value to ensure it's 'length' bytes long
 */
@OptIn(ExperimentalUnsignedTypes::class)
class SBytes(
    mapper: StructMapper,
    length: Int,
    default: UByteArray = ubyteArrayOf(),
    endianness: Char = '|'
) :
    StructElement<UByteArray>(
        { buf, el ->
            if (el.size != 0) {
                var mValue = el.get()
                if (mValue.size > el.size) {
                    mValue = el.get().sliceArray(0 until length - 1) // Truncate if too long
                } else if (mValue.size < length) {
                    mValue += UByteArray(length - el.size)// Pad if too short
                }
                buf.putBytes(mValue)
            }
        },
        { buf, el ->
            el.set(buf.getBytes(el.size))
        },
        mapper, length, default, endianness
    )

/**
 * Represents a fixed size list of T
 * @param T the type (must inherit Mappable)
 */
@OptIn(ExperimentalUnsignedTypes::class)
class SFixedList<T>(mapper: StructMapper, count: Int, default: List<T?> = List(count) { null }) :
    Mappable {
    private val mapIndex = mapper.register(this)
    private val list = default

    init {
        if (count != default.size) throw PacketEncodeException("Fixed list count does not match default value count")
    }

    override fun toBytes(): UByteArray {
        val bytes: MutableList<UByte> = mutableListOf()
        list.forEach {
            if (it != null) {
                bytes += (it as Mappable).toBytes()
            } else {
                println("Warning: FixedList contained null element, ignoring")
            }
        }
        return bytes.toUByteArray()
    }

    override fun fromBytes(bytes: DataBuffer) {
        TODO("Not yet implemented")
    }
}