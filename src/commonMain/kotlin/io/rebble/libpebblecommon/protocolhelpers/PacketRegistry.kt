package io.rebble.libpebblecommon.protocolhelpers

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.packets.*
import io.rebble.libpebblecommon.packets.blobdb.blobDBPacketsRegister
import io.rebble.libpebblecommon.packets.blobdb.timelinePacketsRegister

/**
 * Singleton to track endpoint / type discriminators for deserialization
 */
object PacketRegistry {
    private var typeOffsets: MutableMap<ProtocolEndpoint, Int> = mutableMapOf()
    private var typedDecoders: MutableMap<ProtocolEndpoint, MutableMap<UByte, (UByteArray) -> PebblePacket>> =
        mutableMapOf()
    private var universalDecoders: MutableMap<ProtocolEndpoint, (UByteArray) -> PebblePacket> =
        mutableMapOf()

    init {
        systemPacketsRegister()
        timePacketsRegister()
        timelinePacketsRegister()
        blobDBPacketsRegister()
        appmessagePacketsRegister()
        appRunStatePacketsRegister()
        musicPacketsRegister()
        appFetchIncomingPacketsRegister()
        putBytesIncomingPacketsRegister()
    }

    /**
     * Register a custom offset for the type discriminator (e.g. if the first byte after frame is not the command)
     * @param endpoint the endpoint to register the new offset to
     * @param offset the new offset, including frame offset (4)
     */
    fun registerCustomTypeOffset(endpoint: ProtocolEndpoint, offset: Int) {
        typeOffsets[endpoint] = offset
    }

    fun register(endpoint: ProtocolEndpoint, decoder: (UByteArray) -> PebblePacket) {
        universalDecoders[endpoint] = decoder
    }

    fun register(endpoint: ProtocolEndpoint, type: UByte, decoder: (UByteArray) -> PebblePacket) {
        if (typedDecoders[endpoint] == null) {
            typedDecoders[endpoint] = mutableMapOf()
        }
        typedDecoders[endpoint]!![type] = decoder
    }

    fun get(endpoint: ProtocolEndpoint, packet: UByteArray): PebblePacket {
        universalDecoders[endpoint]?.let { return it(packet) }

        val epdecoders = typedDecoders[endpoint]
            ?: throw PacketDecodeException("No packet class registered for endpoint $endpoint")

        val typeOffset = if (typeOffsets[endpoint] != null) typeOffsets[endpoint]!! else 4
        val decoder = epdecoders[packet[typeOffset]]
            ?: throw PacketDecodeException("No packet class registered for type ${packet[typeOffset]} of $endpoint")
        return decoder(packet)
    }
}