package io.rebble.libpebblecommon.services.appmessage

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppMessage
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class AppMessageService(private val protocolHandler: ProtocolHandler) {
    val receivedMessages = Channel<AppMessage>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_MESSAGE, this::receive)
    }

    /**
     * Send an AppMessage
     */
    suspend fun send(packet: AppMessage) {
        protocolHandler.withWatchContext {
            protocolHandler.send(packet)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun receive(packet: PebblePacket) {
        if (packet !is AppMessage) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.offer(packet)
    }

}