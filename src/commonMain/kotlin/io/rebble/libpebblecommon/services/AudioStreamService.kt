package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AudioStream
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class AudioStreamService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<AudioStream>(Channel.BUFFERED)

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.AUDIO_STREAMING, this::receive)
    }

    suspend fun send(packet: AudioStream) {
        protocolHandler.send(packet)
    }

    fun receive(packet: PebblePacket) {
        if (packet !is AudioStream) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        receivedMessages.trySend(packet)
    }
}