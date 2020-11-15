package io.rebble.libpebblecommon.structmapper

import io.rebble.libpebblecommon.util.DataBuffer

abstract class StructMappable : Mappable {
    val m = StructMapper()

    override fun toBytes(): UByteArray {
        return m.toBytes()
    }

    override fun fromBytes(bytes: DataBuffer) {
        m.fromBytes(bytes)
    }
}