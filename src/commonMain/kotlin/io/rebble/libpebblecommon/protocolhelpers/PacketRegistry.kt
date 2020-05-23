package io.rebble.libpebblecommon.protocolhelpers

import io.rebble.libpebblecommon.blobdb.blobDBPacketsRegister
import io.rebble.libpebblecommon.blobdb.timelinePacketsRegister
import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.systemPacketsRegister

@ExperimentalUnsignedTypes
/**
 * Singleton to track endpoint / type discriminators for deserialization
 */
object PacketRegistry {
    private var typeOffsets: MutableMap<ProtocolEndpoint, Int> = mutableMapOf()
    private var decoders: MutableMap<ProtocolEndpoint, MutableMap<UByte, (UByteArray) -> PebblePacket>> = mutableMapOf()

    fun setup() {
        systemPacketsRegister()
        timelinePacketsRegister()
        blobDBPacketsRegister()
    }

    /**
     * Register a custom offset for the type discriminator (e.g. if the first byte after frame is not the command)
     * @param endpoint the endpoint to register the new offset to
     * @param offset the new offset, including frame offset (4)
     */
    fun registerCustomTypeOffset(endpoint: ProtocolEndpoint, offset: Int) {
        typeOffsets[endpoint] = offset
    }

    fun register(endpoint: ProtocolEndpoint, type: UByte, decoder: (UByteArray) -> PebblePacket) {
        if (decoders[endpoint] == null) {
            decoders[endpoint] = mutableMapOf()
        }
        decoders[endpoint]!![type] = decoder
    }

    fun get(endpoint: ProtocolEndpoint, packet: UByteArray): PebblePacket {
        val epdecoders = decoders[endpoint] ?: throw PacketDecodeException("No packet class registered for endpoint/type combo")

        val typeOffset = if (typeOffsets[endpoint] != null) typeOffsets[endpoint]!! else 4
        val decoder = epdecoders[packet[typeOffset]] ?: throw PacketDecodeException("No packet class registered for endpoint/type combo")
        return decoder(packet)
    }
}