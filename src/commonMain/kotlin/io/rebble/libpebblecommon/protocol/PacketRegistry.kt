package io.rebble.libpebblecommon.protocol

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.systemPacketsRegister

@ExperimentalUnsignedTypes
object PacketRegistry {
    private var decoders: MutableMap<ProtocolEndpoint, MutableMap<UByte, (UByteArray) -> PebblePacket>> = mutableMapOf()

    fun setup() {
        systemPacketsRegister()
    }

    fun register(endpoint: ProtocolEndpoint, type: UByte, decoder: (UByteArray) -> PebblePacket) {
        if (decoders[endpoint] == null) {
            decoders[endpoint] = mutableMapOf()
        }
        decoders[endpoint]!![type] = decoder
    }

    fun get(endpoint: ProtocolEndpoint, packet: UByteArray): PebblePacket {
        val epdecoders = decoders[endpoint] ?: throw PacketDecodeException("No packet class registered for endpoint/type combo")
        val decoder = epdecoders[packet[4]] ?: throw PacketDecodeException("No packet class registered for endpoint/type combo")
        return decoder(packet)
    }
}