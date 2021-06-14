package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppLogReceivedMessage
import io.rebble.libpebblecommon.packets.AppLogShippingControlMessage
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class AppLogService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<AppLogReceivedMessage>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_LOGS, this::receive)
    }

    suspend fun send(packet: AppLogShippingControlMessage) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is AppLogReceivedMessage) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.offer(packet)
    }
}