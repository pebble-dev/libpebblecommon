package io.rebble.libpebblecommon.services.appmessage

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppCustomizationSetStockAppIconMessage
import io.rebble.libpebblecommon.packets.AppCustomizationSetStockAppTitleMessage
import io.rebble.libpebblecommon.packets.AppMessage
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.services.ProtocolService
import kotlinx.coroutines.channels.Channel

class AppMessageService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<AppMessage>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_MESSAGE, this::receive)
    }

    /**
     * Send an AppMessage
     */
    suspend fun send(packet: AppMessage) {
        protocolHandler.send(packet)
    }

    suspend fun send(packet: AppCustomizationSetStockAppIconMessage) {
        protocolHandler.send(packet)
    }

    suspend fun send(packet: AppCustomizationSetStockAppTitleMessage) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is AppMessage) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.trySend(packet)
    }

}