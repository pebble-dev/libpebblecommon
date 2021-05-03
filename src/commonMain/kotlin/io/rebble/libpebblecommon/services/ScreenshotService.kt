package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.ScreenshotRequest
import io.rebble.libpebblecommon.packets.ScreenshotResponse
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class ScreenshotService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<ScreenshotResponse>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.SCREENSHOT, this::receive)
    }

    suspend fun send(packet: ScreenshotRequest) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is ScreenshotResponse) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.offer(packet)
    }

}