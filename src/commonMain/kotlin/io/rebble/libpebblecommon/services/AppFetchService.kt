package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppFetchIncomingPacket
import io.rebble.libpebblecommon.packets.AppFetchOutgoingPacket
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class AppFetchService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<AppFetchIncomingPacket>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_FETCH, this::receive)
    }

    suspend fun send(packet: AppFetchOutgoingPacket) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is AppFetchIncomingPacket) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.trySend(packet)
    }

}