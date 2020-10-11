package io.rebble.libpebblecommon.services.appmessage

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppMessage
import io.rebble.libpebblecommon.packets.blobdb.BlobCommand
import io.rebble.libpebblecommon.packets.blobdb.BlobResponse
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import io.rebble.libpebblecommon.structmapper.SUByte
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class AppMessageService(private val protocolHandler: ProtocolHandler) {
    val receivedMessages = Channel<AppMessage>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_MESSAGE, this::receive)
    }

    /**
     * Send a BlobCommand, with an optional callback to be triggered when a matching BlobResponse is received
     * @see BlobCommand
     * @see BlobResponse
     * @param packet the packet to send
     */
    suspend fun send(packet: AppMessage) {
        protocolHandler.withWatchContext {
            protocolHandler.send(packet)
        }
    }

    /**
     * Intended to be called via a protocol handler, handles BlobResponse packets
     * @return true if the packet was handled, false if it wasn't (e.g. not sent via send())
     * @see send
     * @see BlobResponse
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun receive(packet: PebblePacket) {
        if (packet !is AppMessage) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.offer(packet)
    }

}