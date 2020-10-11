package io.rebble.libpebblecommon.services.app

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppRunStateMessage
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class AppRunStateService(private val protocolHandler: ProtocolHandler) {
    val receivedMessages = Channel<AppRunStateMessage>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_RUN_STATE, this::receive)
    }

    suspend fun send(packet: AppRunStateMessage) {
        protocolHandler.withWatchContext {
            protocolHandler.send(packet)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun receive(packet: PebblePacket) {
        if (packet !is AppRunStateMessage) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.offer(packet)
    }

}