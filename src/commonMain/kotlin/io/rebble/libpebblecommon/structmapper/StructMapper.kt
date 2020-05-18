package io.rebble.libpebblecommon.structmapper

import io.rebble.libpebblecommon.DataBuffer

/**
 * Maps class properties to a struct equivalent
 */
@ExperimentalUnsignedTypes
class StructMapper: Mappable {
    private var struct: MutableList<Mappable> = mutableListOf()
    fun register(type: Mappable): Int {
        struct.add(type)
        return struct.size - 1
    }

    fun getStruct(): List<Mappable> {
        return struct.toList()
    }

    override fun toBytes(endianness: Char): UByteArray {
        var bytes = ubyteArrayOf()
        getStruct().forEach {
            bytes += it.toBytes(endianness)
        }
        return bytes
    }

    override fun fromBytes(bytes: DataBuffer) {
        getStruct().forEach {
            it.fromBytes(bytes)
        }
    }
}