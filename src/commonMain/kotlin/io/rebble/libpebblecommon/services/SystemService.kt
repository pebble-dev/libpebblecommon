package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.SystemPacket
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

/**
 * Singleton to handle sending notifications cleanly, as well as TODO: receiving/acting on action events
 */
@OptIn(ExperimentalUnsignedTypes::class)
class SystemService(private val protocolHandler: ProtocolHandler) {
    val receivedMessages = Channel<SystemPacket>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.PHONE_VERSION, this::receive)
    }

    /**
     * Send an AppMessage
     */
    suspend fun send(packet: SystemPacket) {
        protocolHandler.withWatchContext {
            protocolHandler.send(packet)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun receive(packet: PebblePacket) {
        if (packet !is SystemPacket) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.offer(packet)
    }

}