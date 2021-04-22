package io.rebble.libpebblecommon.services

import io.rebble.libpebblecommon.ProtocolHandler
import io.rebble.libpebblecommon.packets.AppOrderResultCode
import io.rebble.libpebblecommon.packets.AppReorderOutgoingPacket
import io.rebble.libpebblecommon.packets.AppReorderResult
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.protocolhelpers.ProtocolEndpoint
import kotlinx.coroutines.channels.Channel

class AppReorderService(private val protocolHandler: ProtocolHandler) : ProtocolService {
    val receivedMessages = Channel<AppReorderResult>(Channel.BUFFERED)
    private var lastOrderPacket: AppReorderOutgoingPacket? = null

    init {
        protocolHandler.registerReceiveCallback(ProtocolEndpoint.APP_REORDER, this::receive)
    }

    suspend fun send(packet: AppReorderOutgoingPacket) {
        protocolHandler.send(packet)
    }

    suspend fun receive(packet: PebblePacket) {
        if (packet !is AppReorderResult) {
            throw IllegalStateException("Received invalid packet type: $packet")
        }

        if (packet.status.get() == AppOrderResultCode.RETRY.value) {
            lastOrderPacket?.let { send(it) }
            return
        }

        lastOrderPacket = null
        receivedMessages.offer(packet)
    }
}