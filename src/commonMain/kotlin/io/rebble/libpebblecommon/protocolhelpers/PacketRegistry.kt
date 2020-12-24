package io.rebble.libpebblecommon.protocolhelpers

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.packets.appRunStatePacketsRegister
import io.rebble.libpebblecommon.packets.appmessagePacketsRegister
import io.rebble.libpebblecommon.packets.blobdb.blobDBPacketsRegister
import io.rebble.libpebblecommon.packets.blobdb.timelinePacketsRegister
import io.rebble.libpebblecommon.packets.systemPacketsRegister
import io.rebble.libpebblecommon.packets.timePacketsRegister

/**
 * Singleton to track endpoint / type discriminators for deserialization
 */
object PacketRegistry {
    private var typeOffsets: MutableMap<ProtocolEndpoint, Int> = mutableMapOf()
    private var decoders: MutableMap<ProtocolEndpoint, MutableMap<UByte, (UByteArray) -> PebblePacket>> = mutableMapOf()

    init {
        systemPacketsRegister()
        timePacketsRegister()
        timelinePacketsRegister()
        blobDBPacketsRegister()
        appmessagePacketsRegister()
        appRunStatePacketsRegister()
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
        val epdecoders = decoders[endpoint]
            ?: throw PacketDecodeException("No packet class registered for endpoint $endpoint")

        val typeOffset = if (typeOffsets[endpoint] != null) typeOffsets[endpoint]!! else 4
        val decoder = epdecoders[packet[typeOffset]]
            ?: throw PacketDecodeException("No packet class registered for type ${packet[typeOffset]} of $endpoint")
        return decoder(packet)
    }
}