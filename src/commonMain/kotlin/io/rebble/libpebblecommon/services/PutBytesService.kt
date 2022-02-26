package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.PutBytesOutgoingPacket
import io.rebble.libpebblecommon.packets.PutBytesResponse
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class PutBytesService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<PutBytesResponse>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.PUT_BYTES, this::receive)
    }

    suspend fun send(packet: PutBytesOutgoingPacket) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is PutBytesResponse) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.trySend(packet)
    }

}