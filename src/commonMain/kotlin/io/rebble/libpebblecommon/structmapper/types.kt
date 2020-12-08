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
interface Mappable {
    /**
     * Serializes/packs the mappable to its raw equivalent
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

interface NumberStructElement {
    val valueNumber: Long
}

/**
 * Represents a property mappable to a struct via a StructMapper
 * @param endianness represents endianness on serialization
 */
open class StructElement<T>(
    private val putType: (DataBuffer, StructElement<T>) -> Unit,
    private val getType: (DataBuffer, StructElement<T>) -> Unit,
    mapper: StructMapper,
    size: Int,
    default: T,
    endianness: Char = '|'
) : Mappable { //TODO: Element-level endianness on deserialization
    var size = size
        get() {
            return linkedSize?.valueNumber?.toInt() ?: field
        }
        set(value) {
            field = value
            linkedSize = null
        }

    private var linkedSize: NumberStructElement? = null
        private set

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
        buf.setEndian(if (isLittleEndian) '<' else '>')
        putType(buf, this)
        return buf.array()
    }

    override fun fromBytes(bytes: DataBuffer) {
        bytes.setEndian(if (isLittleEndian) '<' else '>')
        getType(bytes, this)
    }

    fun setEndiannes(endianness: Char) {
        isLittleEndian = endianness == '<'
    }

    /**
     * Link the size of this element to the value of another struct element. Size will
     * automatically match value of the target element.
     */
    fun linkWithSize(numberStructElement: NumberStructElement) {
        linkedSize = numberStructElement
    }

    override fun toString(): String {
        return "StructElement(size=$size, linkedSize=${linkedSize?.valueNumber}, " +
                "value=$value, isLittleEndian=$isLittleEndian)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StructElement<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}

class SUByte(mapper: StructMapper, default: UByte = 0u) :
    StructElement<UByte>(
        { buf, el -> buf.putUByte(el.get()) },
        { buf, el -> el.set(buf.getUByte()) },
        mapper,
        UByte.SIZE_BYTES,
        default
    ), NumberStructElement {
    override val valueNumber: Long
        get() = get().toLong()
}

class SByte(mapper: StructMapper, default: Byte = 0) :
    StructElement<Byte>(
        { buf, el -> buf.putByte(el.get()) },
        { buf, el -> el.set(buf.getByte()) },
        mapper,
        Byte.SIZE_BYTES,
        default
    ), NumberStructElement {
    override val valueNumber: Long
        get() = get().toLong()
}

class SUInt(mapper: StructMapper, default: UInt = 0u, endianness: Char = '|') :
    StructElement<UInt>(
        { buf, el -> buf.putUInt(el.get()) },
        { buf, el -> el.set(buf.getUInt()) },
        mapper,
        UInt.SIZE_BYTES,
        default,
        endianness
    ), NumberStructElement {
    override val valueNumber: Long
        get() = get().toLong()
}

class SInt(mapper: StructMapper, default: Int = 0, endianness: Char = '|') :
    StructElement<Int>(
        { buf, el -> buf.putInt(el.get()) },
        { buf, el -> el.set(buf.getInt()) },
        mapper,
        Int.SIZE_BYTES,
        default,
        endianness
    ), NumberStructElement {
    override val valueNumber: Long
        get() = get().toLong()
}

class SULong(mapper: StructMapper, default: ULong = 0u) :
    StructElement<ULong>(
        { buf, el -> buf.putULong(el.get()) },
        { buf, el -> el.set(buf.getULong()) },
        mapper,
        ULong.SIZE_BYTES,
        default
    ), NumberStructElement {
    override val valueNumber: Long
        get() = get().toLong()
}

class SUShort(mapper: StructMapper, default: UShort = 0u, endianness: Char = '|') :
    StructElement<UShort>(
        { buf, el -> buf.putUShort(el.get()) },
        { buf, el -> el.set(buf.getUShort()) },
        mapper,
        UShort.SIZE_BYTES,
        default,
        endianness
    ), NumberStructElement {
    override val valueNumber: Long
        get() = get().toLong()
}

class SShort(mapper: StructMapper, default: Short = 0, endianness: Char = '|') :
    StructElement<Short>(
        { buf, el -> buf.putShort(el.get()) },
        { buf, el -> el.set(buf.getShort()) },
        mapper,
        Short.SIZE_BYTES,
        default,
        endianness = endianness
    ), NumberStructElement {
    override val valueNumber: Long
        get() = get().toLong()
}

class SBoolean(mapper: StructMapper, default: Boolean = false) :
    StructElement<Boolean>(
        { buf, el -> buf.putUByte(if (el.get()) 1u else 0u) },
        { buf, el -> el.set(buf.getUByte() != 0u.toUByte()) },
        mapper,
        UByte.SIZE_BYTES,
        default
    )

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
class SString(mapper: StructMapper, default: String = "") :
    StructElement<String>(
        { buf, el ->
            val bytes = el.get().encodeToByteArray()
            buf.putUByte(bytes.size.toUByte())
            buf.putBytes(
                bytes.toUByteArray()
            )
        },
        { buf, el ->
            val len = buf.getUByte().toInt()
            el.set(buf.getBytes(len).toByteArray().decodeToString(), len)
        }, mapper, default.encodeToByteArray().size + 1, default
    )

/**
 * Represents a string (UTF-8) in a struct with a fixed length
 */
class SFixedString(mapper: StructMapper, size: Int, default: String = "") :
    StructElement<String>(
        { buf, el ->
            var bytes = el.get().encodeToByteArray()
            if (bytes.size > size) {
                bytes = bytes.take(size).toByteArray()
            }

            buf.putBytes(
                bytes.toUByteArray()
            )

            val amountPad = size - bytes.size
            repeat(amountPad) {
                buf.putUByte(0u)
            }
        },
        { buf, el ->
            el.set(
                buf.getBytes(size).toByteArray().takeWhile { it > 0 }.toByteArray()
                    .decodeToString(), size
            )
        }, mapper, size, default
    )

/**
 * Represents arbitrary bytes in a struct
 * @param length the number of bytes, when serializing this is used to pad/truncate the provided value to ensure it's 'length' bytes long
 */
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
                buf.putBytes(if (el.isLittleEndian) mValue.reversedArray() else mValue)
            }
        },
        { buf, el ->
            val value = buf.getBytes(el.size)
            el.set(if (el.isLittleEndian) value.reversedArray() else value)
        },
        mapper, length, default, endianness
    ) {
    override fun toString(): String {
        return "SBytes(value=${get().contentToString()}, size=$size)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SBytes) return false
        if (!this.get().contentEquals(other.get())) return false
        return true
    }

    override fun hashCode(): Int {
        return this.get().hashCode()
    }
}

/**
 * Represents a fixed size list of T
 * @param T the type (must inherit Mappable)
 */
class SFixedList<T : Mappable>(
    mapper: StructMapper,
    count: Int,
    default: List<T> = emptyList(),
    private val itemFactory: () -> T
) :
    Mappable {

    var count = count
        set(value) {
            field = value
            linkedCount = null
        }

    var linkedCount: NumberStructElement? = null
        private set


    private val mapIndex = mapper.register(this)
    var list = default
        private set

    init {
        if (count != default.size) throw PacketEncodeException("Fixed list count does not match default value count")
    }

    override fun toBytes(): UByteArray {
        val bytes: MutableList<UByte> = mutableListOf()
        list.forEach {
            bytes += it.toBytes()
        }
        return bytes.toUByteArray()
    }

    override fun fromBytes(bytes: DataBuffer) {
        val count = linkedCount?.valueNumber?.toInt() ?: count
        list = List(count) {
            itemFactory().apply { fromBytes(bytes) }
        }
    }

    /**
     * Link the count of this element to the value of another struct element. Count will
     * automatically match value of the target element.
     */
    fun linkWithCount(numberStructElement: NumberStructElement) {
        linkedCount = numberStructElement
    }

    override fun toString(): String {
        return "SFixedList(list=$list)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SFixedList<*>) return false

        if (list != other.list) return false

        return true
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }
}