package io.rebble.libpebblecommon.protocolhelpers

import io.rebble.libpebblecommon.util.DataBuffer
import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.exceptions.PacketEncodeException
import io.rebble.libpebblecommon.structmapper.SUShort
import io.rebble.libpebblecommon.structmapper.StructMapper

/**
 * Represents a pebble protocol packet
 */
@ExperimentalUnsignedTypes
open class PebblePacket{
    val endpoint: ProtocolEndpoint
    val m = StructMapper()
    var type: UByte? = null

    constructor(endpoint: ProtocolEndpoint) { //TODO: Packet-level endianness?
        this.endpoint = endpoint
    }
    constructor(packet: UByteArray, endianness: Char = '>') {
        val meta = StructMapper()
        val length = SUShort(meta)
        val ep = SUShort(meta)
        meta.fromBytes(DataBuffer(packet))
        if (length.get() != (packet.size - (UShort.SIZE_BYTES*2)).toUShort())
            throw IllegalArgumentException("Length in packet does not match packet actual size, likely malformed")

        println("Importing packet: Len $length | EP $ep")

        this.endpoint =
            ProtocolEndpoint.getByValue(ep.get())
    }

    /**
     * Serializes a framed pebble protocol packet into a byte array
     * @return The serialized packet
     */
    fun serialize(): UByteArray {
        val content = m.toBytes()
        if (content.isEmpty()) throw PacketEncodeException("Malformed packet: contents empty")
        val meta = StructMapper()
        val length = SUShort(meta, content.size.toUShort())
        val ep = SUShort(meta, endpoint.value)

        return meta.toBytes() + content // Whole packet (meta + content)
    }

    companion object {
        /**
         * Deserializes a framed pebble protocol packet into a PebblePacket class
         * @param packet the packet to deserialize
         * @return The deserialized packet
         */
        fun deserialize(packet: UByteArray): PebblePacket {
            val buf = DataBuffer(packet)

            val meta = StructMapper()
            val length = SUShort(meta)
            val ep = SUShort(meta)
            meta.fromBytes(buf)
            if (packet.size <= (2*UShort.SIZE_BYTES))
                throw PacketDecodeException("Malformed packet: contents empty")
            if (length.get().toInt() != (packet.size - (2*UShort.SIZE_BYTES)))
                throw PacketDecodeException("Malformed packet: bad length")
            val ret = PacketRegistry.get(
                ProtocolEndpoint.getByValue(ep.get()),
                packet
            )
            ret.m.fromBytes(buf)
            return ret
        }
    }
}