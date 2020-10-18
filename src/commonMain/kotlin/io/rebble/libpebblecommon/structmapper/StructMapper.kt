package io.rebble.libpebblecommon.structmapper

import io.rebble.libpebblecommon.util.DataBuffer

/**
 * Maps class properties to a struct equivalent
 */
@OptIn(ExperimentalUnsignedTypes::class)
class StructMapper: Mappable {
    private var struct: MutableList<Mappable> = mutableListOf()

    /**
     * Register a mappable object with the StructMapper (tracks index + auto serialization based on declaration order)
     * Ideally only use this within mappable type definitions
     */
    fun register(type: Mappable): Int {
        struct.add(type)
        return struct.size - 1
    }

    /**
     * Get the struct as a list of Mappables
     * @return The list of mappables
     */
    fun getStruct(): List<Mappable> {
        return struct.toList()
    }

    override fun toBytes(): UByteArray {
        var bytes = ubyteArrayOf()
        getStruct().forEach {
            bytes += it.toBytes()
        }
        return bytes
    }

    override fun fromBytes(bytes: DataBuffer) {
        getStruct().forEach {
            it.fromBytes(bytes)
        }
    }
}