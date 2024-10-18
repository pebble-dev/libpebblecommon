package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.*
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class VoiceService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<IncomingVoicePacket>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.VOICE_CONTROL, this::receive)
    }

    suspend fun send(packet: OutgoingVoicePacket) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is IncomingVoicePacket) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.trySend(packet)
    }
}